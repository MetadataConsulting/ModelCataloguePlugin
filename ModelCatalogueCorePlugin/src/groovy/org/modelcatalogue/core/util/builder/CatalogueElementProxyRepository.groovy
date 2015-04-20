package org.modelcatalogue.core.util.builder

import grails.gorm.DetachedCriteria
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.springframework.util.StopWatch

@Log4j
class CatalogueElementProxyRepository {

    static Set<Class> HAS_UNIQUE_NAMES = [MeasurementUnit, Classification]
    private static final Map LATEST = [sort: 'versionNumber', order: 'desc', max: 1]

    private final ClassificationService classificationService
    private final ElementService elementService

    Set<Class> unclassifiedQueriesFor = []

    private Set<CatalogueElementProxy> pendingProxies = []

    private boolean copyRelationships = false

    private final Map<String, Relationship> createdRelationships = [:]

    CatalogueElementProxyRepository(ClassificationService classificationService, ElementService elementService) {
        this.classificationService = classificationService
        this.elementService = elementService
    }

    public void clear() {
        unclassifiedQueriesFor.clear()
        createdRelationships.clear()
        pendingProxies.clear()
        copyRelationships = false
    }

    public copyRelationships() {
        this.copyRelationships = true
    }

    public static boolean equals(CatalogueElementProxy a, CatalogueElementProxy b) {
        if (a == b) {
            return true
        }
        if (a && !b || b && !a) {
            return false
        }
        if (a.id && a.id == b.id) {
            return true
        }
        if (a.domain != b.domain) {
            return false
        }
        if (a.domain in HAS_UNIQUE_NAMES) {
            return a.name == b.name
        }

        if (!a.classification || !b.classification) {
            return false
        }

        return a.classification == b.classification && a.name == b.name
    }

    public Set<CatalogueElement> resolveAllProxies(boolean skipDirtyChecking) {
        StopWatch watch =  new StopWatch('catalogue proxy repository')
        Set<CatalogueElement> created = []

        Set<CatalogueElementProxy> elementProxiesToBeResolved     = []
        Map<String, CatalogueElementProxy> byID     = [:]
        Map<String, CatalogueElementProxy> byName   = [:]

        watch.start('merging proxies')
        log.info "(1/6) merging proxies"
        for (CatalogueElementProxy proxy in pendingProxies) {
            if (proxy.id) {
                CatalogueElementProxy existing = byID[proxy.id]

                if (!existing) {
                    byID[proxy.id] = proxy
                    elementProxiesToBeResolved << proxy
                } else {
                    existing.merge(proxy)
                }
            } else if (proxy.name) {
                String fullName = "${proxy.domain.simpleName}:${proxy.domain in HAS_UNIQUE_NAMES ? '*' : proxy.classification}:${proxy.name}"
                String genericName = "${CatalogueElement.simpleName}:${proxy.domain in HAS_UNIQUE_NAMES ? '*' : proxy.classification}:${proxy.name}"

                CatalogueElementProxy existing = byName[fullName]

                if (!existing && fullName != genericName) {
                    existing = byName[genericName]
                    if (existing && !existing.domain.isAssignableFrom(proxy.domain)) {
                        existing = null
                    }
                }

                if (!existing) {
                    byName[fullName] = proxy
                    byName[genericName] = proxy
                    // it is a set, so if we add it twice it does not matter
                    elementProxiesToBeResolved << proxy
                } else {
                    // must survive double addition
                    existing.merge(proxy)
                }
            } else {
                throw new IllegalStateException("Proxy $proxy does not provide ID nor name")
            }
        }
        watch.stop()

        if (!skipDirtyChecking) {
            // Step 1:check something changed this must run before any other resolution happens
            watch.start('dirty checking')
            log.info "(2/6) dirty checking"
            for (CatalogueElementProxy element in elementProxiesToBeResolved) {
                if (element.changed) {
                    element.requestDraft()
                }
            }
            watch.stop()

            // Step 2: if something changed, create new versions. if run in one step, it generates false changes
            watch.start('requesting drafts')
            log.info "(3/6) requesting drafts"
            for (CatalogueElementProxy element in elementProxiesToBeResolved) {
                element.createDraftIfRequested()
            }
            watch.stop()
        }

        Set<RelationshipProxy> relationshipProxiesToBeResolved = []

        // Step 3: resolve elements (set properties, update metadata)
        watch.start('resolving elements')
        log.info "(4/6) resolving elements"
        int elNumberOfPositions = Math.floor(Math.log10(elementProxiesToBeResolved.size())) + 2
        elementProxiesToBeResolved.eachWithIndex { CatalogueElementProxy element, i ->
            log.debug "[${(i + 1).toString().padLeft(elNumberOfPositions, '0')}/${elementProxiesToBeResolved.size().toString().padLeft(elNumberOfPositions, '0')}] Resolving $element"
            created << element.resolve()
            relationshipProxiesToBeResolved.addAll element.pendingRelationships
        }
        watch.stop()

        // Step 4: resolve pending relationships
        watch.start('resolving relationships')
        log.info "(5/6) resolving relationships"
        int relNumberOfPositions = Math.floor(Math.log10(relationshipProxiesToBeResolved.size())) + 2
        relationshipProxiesToBeResolved.eachWithIndex { RelationshipProxy relationshipProxy, i ->
            log.debug "[${(i + 1).toString().padLeft(relNumberOfPositions, '0')}/${relationshipProxiesToBeResolved.size().toString().padLeft(relNumberOfPositions, '0')}] Resolving $relationshipProxy"
            relationshipProxy.resolve(this)
        }
        watch.stop()

        // Step 4: resolve state changes
        watch.start('resolving state changes')
        log.info "(6/6) resolving state changes"
        elementProxiesToBeResolved.eachWithIndex { CatalogueElementProxy element, i ->
            log.debug "[${(i + 1).toString().padLeft(elNumberOfPositions, '0')}/${elementProxiesToBeResolved.size().toString().padLeft(elNumberOfPositions, '0')}] Resolving status changes for $element"

            ElementStatus status = element.getParameter('status') as ElementStatus
            CatalogueElement catalogueElement = element.resolve()

            if (status && catalogueElement.status != status) {
                if (status == ElementStatus.FINALIZED) {
                    elementService.finalizeElement(catalogueElement)
                } else if (status == ElementStatus.DRAFT) {
                    elementService.createDraftVersion(catalogueElement, DraftContext.userFriendly())
                } else if (status == ElementStatus.DEPRECATED) {
                    elementService.archive(catalogueElement)
                }
            }
        }
        watch.stop()

        log.info "Proxies resolved:\n${watch.prettyPrint()}"

        created
    }

    public <T extends CatalogueElement> CatalogueElementProxy<T> createProxy(Class<T> domain, Map<String, Object> parameters) {
        CatalogueElementProxy<T> proxy = createAbstractionInternal(domain, parameters)
        pendingProxies << proxy
        proxy
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionInternal(Class<T> domain, Map<String, Object> parameters) {
        if (parameters.id) {
            return createAbstractionById(domain, parameters.name?.toString(), parameters.id?.toString())
        } else if (parameters.classification) {
            return createAbstractionByClassificationAndName(domain, parameters.classification?.toString(), parameters.name?.toString())
        } else if (parameters.name) {
            return createAbstractionByName(domain, parameters.name?.toString())
        }
        throw new IllegalArgumentException("Cannot create element abstraction from $parameters")
    }


    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionById(Class<T> domain, String name, String id) {
        return new DefaultCatalogueElementProxy<T>(this, domain, id, null, name)
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByClassificationAndName(Class<T> domain, String classificationName, String name) {
        return new DefaultCatalogueElementProxy<T>(this, domain, null, classificationName, name)
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByName(Class<T> domain, String name) {
        return new DefaultCatalogueElementProxy<T>(this, domain, null, null, name)
    }


    public static <T extends CatalogueElement> T save(T element) {
        FriendlyErrors.withFriendlyFailure {
            element.save(flush: true, failOnError: true)
        }
    }

    public <T extends CatalogueElement> T createDraftVersion(T element) {
        elementService.createDraftVersion(element, copyRelationships ? DraftContext.userFriendly() : DraftContext.importFriendly(), !copyRelationships )
    }

    protected  <T extends CatalogueElement> T tryFind(Class<T> type, Object classificationName, Object name, Object id) {
        if (type in HAS_UNIQUE_NAMES) {
            return tryFindWithClassification(type, null, name, id)
        }
        tryFindWithClassification(type, Classification.findAllByName(classificationName?.toString()), name, id)
    }

    protected <T extends CatalogueElement> T tryFindUnclassified(Class<T> type, Object name, Object id) {
        tryFindWithClassification(type, null, name, id)
    }

    protected <T extends CatalogueElement> T tryFindWithClassification(Class<T> type, List<Classification> classifications, Object name, Object id) {
        if (id) {
            T result = findById(type, id)
            if (result) {
                return result
            }
        }
        if (!name) {
            return null
        }

        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'name', name.toString()
        }

        if (classifications) {
            T result = getLatestFromCriteria(classificationService.classified(criteria, ClassificationFilter.includes(classifications)))

            if (result) {
                if (!id || !result.modelCatalogueId) {
                    return result
                }
                return null
            }

            // we are looking for results within classification, no way to go if not found
            if (!(type in unclassifiedQueriesFor)) {
                return null
            }
        }

        T result = getLatestFromCriteria(criteria, true)

        // nothing found
        if (!result) {
            return null
        }

        // only return unclassified results
        if (result.classifications && !(type in HAS_UNIQUE_NAMES)) {
            return null
        }

        // return only if there is no id or the modelCatalogueId is null
        if (!id || !result.modelCatalogueId) {
            return result
        }

        // not found
        return null
    }

    protected <T extends CatalogueElement> T findById(Class<T> type, Object id) {
        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'modelCatalogueId', id.toString()
        }
        T result = getLatestFromCriteria(criteria)

        if (result) {
            return result
        }

        // try to find it as it is a default id

        def match = id.toString() =~ /\/(.\w+)\/(\d+)(\.(\d+))?$/

        if (match) {
            Long theId      = match[0][2] as Long
            Integer version = match[0][4] as Integer

            if (version) {
                result = CatalogueElement.findByLatestVersionIdAndVersionNumber(theId, version) as T
                if (result && result.getDefaultModelCatalogueId(false) == id.toString()) {
                    return result
                }
                return null
            }

            result = CatalogueElement.findByLatestVersionId(theId, [sort: 'versionNumber', order: 'desc']) as T

            if (result && id.toString().startsWith(result.getDefaultModelCatalogueId(true))) {
                return result
            }

            result = CatalogueElement.get(theId) as T
            if (result && result.getDefaultModelCatalogueId(true) == id.toString()) {
                return result
            }
        }
        return null
    }

    private static <T extends CatalogueElement> T getLatestFromCriteria(DetachedCriteria<T> criteria, boolean unclassifiedOnly = false) {
        Map<String, Object> params = unclassifiedOnly ? LATEST - [max: 1] : LATEST
        List<T> elements = criteria.list(params)
        if (elements) {
            if (!unclassifiedOnly || criteria.persistentEntity.javaClass in HAS_UNIQUE_NAMES) {
                return elements.first()
            }
            for (T element in elements) {
                if (!element.classifications) {
                    return element
                }
            }
        }
        return null
    }


    public <T extends CatalogueElement,U extends CatalogueElement> Relationship resolveRelationship(RelationshipProxy<T,U> proxy) {
        RelationshipType type = RelationshipType.readByName(proxy.relationshipTypeName)

        T sourceElement = proxy.source.resolve()
        U destinationElement = proxy.destination.resolve()

        if (!sourceElement.readyForQueries) {
            throw new IllegalStateException("Source element $sourceElement is not ready to be part of the relationship ${toString()}")
        }

        if (!destinationElement.readyForQueries) {
            throw new IllegalStateException("Destination element $destinationElement is not ready to be part of the relationship ${toString()}")
        }

        String hash = DraftContext.hashForRelationship(sourceElement, destinationElement, type)


        Relationship existing = createdRelationships[hash]

        if (existing) {
            return existing
        }

        Relationship relationship = sourceElement.createLinkTo(destinationElement, type, resetIndices: true, skipUniqueChecking: proxy.source.new || proxy.destination.new)

        createdRelationships[hash] = relationship

        return relationship
    }

}
