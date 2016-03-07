package org.modelcatalogue.core.util.builder

import com.google.common.collect.ImmutableSet
import grails.compiler.GrailsCompileStatic
import grails.gorm.DetachedCriteria
import groovy.transform.CompileDynamic
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.springframework.util.StopWatch

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass

@Log4j @GrailsCompileStatic
class CatalogueElementProxyRepository {


    static Set<Class> HAS_UNIQUE_NAMES = new LinkedHashSet<Class>([MeasurementUnit, DataModel])

    static final Set<Class> DATA_TYPE_CLASSES = new LinkedHashSet<Class>([EnumeratedType, ReferenceType, DataType, PrimitiveType])

    static final String AUTOMATIC_NAME_FLAG = '__automatic_name__'
    static final String AUTOMATIC_DESCRIPTION_FLAG = '__automatic_description__'
    static final String MISSING_REFERENCE_ID = "http://www.modelcatalogue.org/builder/#missing_reference_id"

    private static final Map LATEST = [sort: 'versionNumber', order: 'desc', max: 1]

    private final DataModelService dataModelService
    private final ElementService elementService

    Set<Class> unclassifiedQueriesFor = []

    private Set<CatalogueElementProxy> pendingProxies = []
    private Map<String, String> semanticVersions = [:]

    private Set<Long> elementsUnderControl = []

    private boolean copyRelationships = false

    private final Map<String, Relationship> createdRelationships = [:]

    ProgressMonitor monitor = ProgressMonitor.NOOP

    CatalogueElementProxyRepository(DataModelService dataModelService, ElementService elementService) {
        this.dataModelService = dataModelService
        this.elementService = elementService
    }

    public void clear() {
        unclassifiedQueriesFor.clear()
        createdRelationships.clear()
        pendingProxies.clear()
        elementsUnderControl.clear()
        copyRelationships = false
    }

    public copyRelationships() {
        this.copyRelationships = true
    }

    public isCopyRelationship() {
        this.copyRelationships
    }

    public static boolean equals(CatalogueElementProxy a, CatalogueElementProxy b) {
        if (a == b) {
            return true
        }
        if (a && !b || b && !a) {
            return false
        }
        if (a.modelCatalogueId && a.modelCatalogueId == b.modelCatalogueId) {
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
        logInfo "(1/6) merging proxies"
        for (CatalogueElementProxy proxy in pendingProxies) {
            if (proxy.modelCatalogueId) {
                CatalogueElementProxy existing = byID[proxy.modelCatalogueId]

                if (!existing) {
                    byID[proxy.modelCatalogueId] = proxy
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
            logInfo "(2/6) dirty checking"
            for (CatalogueElementProxy element in elementProxiesToBeResolved) {
                try {
                    if (element.changed) {
                        element.requestDraft()
                    }
                } catch (e) {
                    if (anyCause(e, ReferenceNotPresentInTheCatalogueException)) {
                        logWarn "Reference ${element} not present in the catalogue"
                    } else {
                        throw e
                    }
                }
                if (element.domain == DataModel && !semanticVersions[element.name] && element.getParameter('semanticVersion')) {
                    semanticVersions[element.name] = element.getParameter('semanticVersion').toString()
                }
            }
            watch.stop()

            // Step 2: if something changed, create new versions. if run in one step, it generates false changes
            watch.start('requesting drafts')
            logInfo "(3/6) requesting drafts"

            elementProxiesToBeResolved.each {
                if (!it.underControl) {
                    return
                }
                try {
                    CatalogueElement e = it.findExisting() as CatalogueElement

                    if (e) {
                        if (e.getLatestVersionId()) {
                            elementsUnderControl << e.getLatestVersionId()
                        } else {
                            elementsUnderControl << e.getId()
                        }
                    }
                } catch (e) {
                    if (anyCause(e, ReferenceNotPresentInTheCatalogueException)) {
                        logWarn "Reference ${it} not present in the catalogue"
                    } else {
                        throw e
                    }
                }
            }

            for (CatalogueElementProxy element in elementProxiesToBeResolved) {
                element.createDraftIfRequested()
            }
            watch.stop()
        }

        Set<RelationshipProxy> relationshipProxiesToBeResolved = []

        // Step 3: resolve elements (set properties, update metadata)
        watch.start('resolving elements')
        logInfo "(4/6) resolving elements"
        int elNumberOfPositions = Math.floor(Math.log10(elementProxiesToBeResolved.size())).intValue() + 2
        elementProxiesToBeResolved.eachWithIndex { CatalogueElementProxy element, i ->
            logDebug "[${(i + 1).toString().padLeft(elNumberOfPositions, '0')}/${elementProxiesToBeResolved.size().toString().padLeft(elNumberOfPositions, '0')}] Resolving $element"
            try {
                created.add(element.resolve() as CatalogueElement)
                relationshipProxiesToBeResolved.addAll element.pendingRelationships
            } catch (e) {
                if (anyCause(e, ReferenceNotPresentInTheCatalogueException)) {
                    logWarn "Reference ${element} not present in the catalogue"
                } else {
                    throw e
                }
            }
        }
        watch.stop()

        // Step 4: resolve pending relationships
        watch.start('resolving relationships')
        Set<Long> resolvedRelationships = []
        logInfo "(5/6) resolving relationships"
        int relNumberOfPositions = Math.floor(Math.log10(relationshipProxiesToBeResolved.size())).intValue() + 2
        relationshipProxiesToBeResolved.eachWithIndex { RelationshipProxy relationshipProxy, i ->
            logDebug "[${(i + 1).toString().padLeft(relNumberOfPositions, '0')}/${relationshipProxiesToBeResolved.size().toString().padLeft(relNumberOfPositions, '0')}] Resolving $relationshipProxy"
            try {
                resolvedRelationships << relationshipProxy.resolve(this)?.getId()
            } catch (e) {
                if (anyCause(e,ReferenceNotPresentInTheCatalogueException)) {
                    logWarn "Some item referred by ${relationshipProxy} not present in the catalogue"
                } else {
                    throw e
                }
            }
        }

        // TODO: collect the ids of relationships resolved and than do the same comparison like in the is relationship
        // changed
        if (!copyRelationships) {
            elementProxiesToBeResolved.eachWithIndex { CatalogueElementProxy element, i ->
                if (!element.underControl) {
                    return
                }
                CatalogueElement catalogueElement = element.resolve() as CatalogueElement
                Set<Long> relations = []
                relations.addAll catalogueElement.incomingRelationships*.getId()
                relations.addAll catalogueElement.outgoingRelationships*.getId()

                relations.removeAll resolvedRelationships

                relations.collect { Relationship.get(it) } each {
                    unlink(it)
                }
            }
        }

        watch.stop()

        // Step 4: resolve state changes
        watch.start('resolving state changes')
        logInfo "(6/6) resolving state changes"
        elementProxiesToBeResolved.eachWithIndex { CatalogueElementProxy element, i ->
            logDebug "[${(i + 1).toString().padLeft(elNumberOfPositions, '0')}/${elementProxiesToBeResolved.size().toString().padLeft(elNumberOfPositions, '0')}] Resolving status changes for $element"

            ElementStatus status = element.getParameter('status') as ElementStatus

            try {
                CatalogueElement catalogueElement = element.resolve() as CatalogueElement

                if (status && catalogueElement.status != status) {
                    if (status == ElementStatus.FINALIZED) {
                        elementService.finalizeElement(catalogueElement)
                    } else if (status == ElementStatus.DRAFT) {
                        elementService.createDraftVersion(catalogueElement, DraftContext.userFriendly())
                    } else if (status == ElementStatus.DEPRECATED) {
                        elementService.archive(catalogueElement, true)
                    }
                }
            } catch (e) {
                if (anyCause(e, ReferenceNotPresentInTheCatalogueException)) {
                    // already printed
                } else {
                    throw e
                }
            }


        }
        watch.stop()

        logInfo "Proxies resolved:\n${watch.prettyPrint()}"

        created
    }


    private Relationship unlink(Relationship relationship) {
        elementService.relationshipService.unlink(relationship.source as CatalogueElement, relationship.destination as CatalogueElement, relationship.relationshipType as RelationshipType, relationship.dataModel, relationship.archived)
    }

    public <T extends CatalogueElement> CatalogueElementProxy<T> createProxy(Class<T> domain, Map<String, Object> parameters, boolean underControl = false) {
        CatalogueElementProxy<T> proxy = createAbstractionInternal(domain, parameters, underControl)
        pendingProxies << proxy
        proxy
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionInternal(Class<T> domain, Map<String, Object> parameters, boolean underControl = false) {
        if (parameters.id) {
            return createAbstractionById(domain, parameters.name?.toString(), parameters.id?.toString(), underControl)
        } else if (parameters.classification || parameters.dataModel) {
            return createAbstractionByClassificationAndName(domain, (parameters.classification ?: parameters.dataModel)?.toString(), parameters.name?.toString(), underControl)
        } else if (parameters.name) {
            return createAbstractionByName(domain, parameters.name?.toString(), underControl)
        }
        throw new IllegalArgumentException("Cannot create element abstraction from $parameters")
    }


    public <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionById(Class<T> domain, String name, String id, boolean underControl) {
        return new DefaultCatalogueElementProxy<T>(this, domain, id, null, name, underControl)
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByClassificationAndName(Class<T> domain, String classificationName, String name, boolean underControl) {
        return new DefaultCatalogueElementProxy<T>(this, domain, null, classificationName, name, underControl)
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByName(Class<T> domain, String name, boolean underControl) {
        return new DefaultCatalogueElementProxy<T>(this, domain, null, null, name, underControl)
    }


    public static <T extends CatalogueElement> T save(T element) {
        FriendlyErrors.withFriendlyFailure {
            element.save(/* flush: true, */ failOnError: true, deepValidate: false)
        } as T
    }

    public <T extends CatalogueElement> T createDraftVersion(T element, CatalogueElementProxy proxy) {
        elementService.createDraftVersion(element, createDraftContext(proxy, element).version(semanticVersions[proxy.classification]))
    }

    private <T extends CatalogueElement> DraftContext createDraftContext(CatalogueElementProxy proxy, T element) {
        if (copyRelationships) {
            if (proxy.domain == getEntityClass(element)) {
                return DraftContext.userFriendly()
            }
            if ((element.getLatestVersionId() ?: element.getId()) in elementsUnderControl) {
                return DraftContext.typeChangingUserFriendly(proxy.domain)
            }
            return DraftContext.userFriendly()
        }
        if (proxy.domain == getEntityClass(element)) {
            return DraftContext.importFriendly(elementsUnderControl)
        }
        if ((element.getLatestVersionId() ?: element.getId()) in elementsUnderControl) {
            return DraftContext.typeChangingImportFriendly(proxy.domain, elementsUnderControl)
        }
        return DraftContext.importFriendly(elementsUnderControl)
    }

    protected  <T extends CatalogueElement> T tryFind(Class<T> type, Object classificationName, Object name, Object id) {
        if (type in HAS_UNIQUE_NAMES) {
            return tryFindWithClassification(type, null, name, id)
        }
        tryFindWithClassification(type, DataModel.findAllByName(classificationName?.toString()), name, id)
    }

    protected <T extends CatalogueElement> T tryFindUnclassified(Class<T> type, Object name, Object id) {
        tryFindWithClassification(type, null, name, id)
    }

    protected <T extends CatalogueElement> T tryFindWithClassification(Class<T> type, List<DataModel> dataModels, Object name, Object id) {
        if (type in DATA_TYPE_CLASSES) {
            type = DataType
        }
        if (id) {
            T result = findById(type, id)
            if (result) {
                return result
            }
        }
        if (!name) {
            return null
        }

        DetachedCriteria<T> criteria = getNameCriteria(type, name)

        if (dataModels) {
            T result = getLatestFromCriteria(dataModelService.classified(criteria, DataModelFilter.includes(dataModels)))

            if (result) {
                if (!id || !result.hasModelCatalogueId()) {
                    return result
                }
                return null
            }

            // we are looking for results within classification, no way to go if not found
            if (!(type in unclassifiedQueriesFor)) {
                return null
            }
        }

        T result = getLatestFromCriteria(getNameCriteria(type, name), true)

        // nothing found
        if (!result) {
            return null
        }

        // only return unclassified results
        if (result.dataModel && !(type in HAS_UNIQUE_NAMES)) {
            return null
        }

        // return only if there is no id or the modelCatalogueId is null
        if (!id || !result.hasModelCatalogueId()) {
            return result
        }

        // not found
        return null
    }

    @CompileDynamic
    private static <T> DetachedCriteria<T> getNameCriteria(Class<T> type, name) {
        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'name', name.toString()
        }
        criteria
    }

    protected <T extends CatalogueElement> T findById(Class<T> type, Object id) {
        elementService.findByModelCatalogueId(type, id?.toString())?.asType(type) as T
    }

    private static <T extends CatalogueElement> T getLatestFromCriteria(DetachedCriteria<T> criteria, boolean unclassifiedOnly = false) {
        Map<String, Object> params = unclassifiedOnly ? LATEST - [max: 1] : LATEST
        List<T> elements = criteria.list(params)
        if (elements) {
            if (!unclassifiedOnly || criteria.persistentEntity.javaClass in HAS_UNIQUE_NAMES) {
                return elements.first() as T
            }
            for (T element in elements) {
                if (!element.dataModel) {
                    return element
                }
            }
        }
        return null
    }


    public <T extends CatalogueElement,U extends CatalogueElement> Relationship resolveRelationship(RelationshipProxy<T,U> proxy) {
        RelationshipType type = RelationshipType.readByName(proxy.relationshipTypeName) as RelationshipType

        T sourceElement = proxy.source.resolve()
        U destinationElement = proxy.destination.resolve()

        if (sourceElement == destinationElement) {
            throw new IllegalStateException("Source and the destinaiton is the same: $sourceElement")
        }

        if (sourceElement.hasErrors()) {
            throw new IllegalStateException(FriendlyErrors.printErrors("Source element $sourceElement contains errors and is not ready to be part of the relationship ${proxy.toString()}", sourceElement.errors))
        }

        if (!sourceElement.readyForQueries) {
            throw new IllegalStateException("Source element $sourceElement is not ready to be part of the relationship ${proxy.toString()}")
        }
        if (destinationElement.hasErrors()) {
            throw new IllegalStateException(FriendlyErrors.printErrors("Destination element $destinationElement contains errors and is not ready to be part of the relationship ${proxy.toString()}", destinationElement.errors))
        }

        if (!destinationElement.readyForQueries) {
            throw new IllegalStateException("Destination element $destinationElement is not ready to be part of the relationship ${proxy.toString()}")
        }

        String hash = DraftContext.hashForRelationship(sourceElement, destinationElement, type)


        Relationship existing = createdRelationships[hash]

        if (existing) {
            return existing
        }



        Relationship relationship = sourceElement.createLinkTo(destinationElement, type, archived: proxy.archived as Object, resetIndices: true, skipUniqueChecking: (proxy.source.new || proxy.destination.new) as Object)

        createdRelationships[hash] = relationship

        return relationship
    }

    private static boolean anyCause(Throwable th, Class<? extends Throwable> error) {
        if (error.isInstance(th)) {
            return true
        }
        if (!th.cause) {
            return false
        }
        return anyCause(th.cause, error)
    }

    def static <T extends CatalogueElement> T findByMissingReferenceId(String missingReferenceId) {
        (T) ExtensionValue.findByNameAndExtensionValue(MISSING_REFERENCE_ID, missingReferenceId)?.element
    }

    void logDebug(String string) {
        monitor.log(string)
        log.debug string
    }

    void logInfo(String string) {
        monitor.log(string)
        log.info string
    }

    void logWarn(String string) {
        monitor.log(string)
        log.warn string
    }


}
