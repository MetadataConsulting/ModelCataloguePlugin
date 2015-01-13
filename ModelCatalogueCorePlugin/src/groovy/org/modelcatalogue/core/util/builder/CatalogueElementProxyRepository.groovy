package org.modelcatalogue.core.util.builder

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.publishing.DraftContext

class CatalogueElementProxyRepository {

    private Set<Class> HAS_UNIQUE_NAMES = [MeasurementUnit, Classification]
    private static final Map LATEST = [sort: 'versionNumber', order: 'desc', max: 1]

    private final ClassificationService classificationService
    private final ElementService elementService

    Set<Class> unclassifiedQueriesFor = []

    private Set<CatalogueElementProxy> pendingProxies = []

    CatalogueElementProxyRepository(ClassificationService classificationService, ElementService elementService) {
        this.classificationService = classificationService
        this.elementService = elementService
    }

    public void clear() {
        unclassifiedQueriesFor.clear()
        pendingProxies.clear()
    }

    public boolean equals(CatalogueElementProxy a, CatalogueElementProxy b) {
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

    public Set<CatalogueElement> resolveAllProxies() {
        Set<CatalogueElement> created = []

        Set<CatalogueElementProxy> toBeResolved     = []
        Map<String, CatalogueElementProxy> byID     = [:]
        Map<String, CatalogueElementProxy> byName   = [:]

        for (CatalogueElementProxy proxy in pendingProxies) {
            if (proxy.id) {
                CatalogueElementProxy existing = byID[proxy.id]

                if (!existing) {
                    byID[proxy.id] = proxy
                    toBeResolved << proxy
                } else {
                    existing.merge(proxy)
                }
            }

            String fullName = "${proxy.domain}:${proxy.domain in HAS_UNIQUE_NAMES ? '*' : proxy.classification}:${proxy.name}"
            CatalogueElementProxy existing = byName[fullName]

            if (!existing) {
                byName[fullName] = proxy
                // it is a set, so if we add it twice it does not matter
                toBeResolved << proxy
            } else {
                // must survive double addition
                existing.merge(proxy)
            }
        }

        // Step 1:check something changed this must run before any other resolution happens
        for (CatalogueElementProxy element in toBeResolved) {
            if (element.changed) {
                element.requestDraft()
            }
        }

        // Step 2: if something changed, create new versions. if run in one step, it generates false changes
        for (CatalogueElementProxy element in toBeResolved) {
            element.createDraftIfRequested()
        }


        // Step 3: resolve elements (set properties, update metadata)
        for (CatalogueElementProxy element in toBeResolved) {
            created << element.resolve()
        }

        // Step 4: resolve pending relationships
        for (CatalogueElementProxy element in toBeResolved) {
            element.resolveRelationships()
        }

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


    public <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionById(Class<T> domain, String name, String id) {
        return new DefaultCatalogueElementProxy<T>(this, domain, id, null, name)
    }

    public <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByClassificationAndName(Class<T> domain, String classificationName, String name) {
        return new DefaultCatalogueElementProxy<T>(this, domain, null, classificationName, name)
    }

    public <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByName(Class<T> domain, String name) {
        return new DefaultCatalogueElementProxy<T>(this, domain, null, null, name)
    }


    public static <T extends CatalogueElement> T save(T element) {
        element.save(flush: true, failOnError: true)
    }

    public <T extends CatalogueElement> T createDraftVersion(T element) {
        DraftContext context = DraftContext.importFriendly()
        T draft = element.createDraftVersion(elementService, context) as T
        if (draft.hasErrors()) {
            throw new IllegalStateException("Failed to create draft version of $element. Errors: $draft.errors")
        }
        context.classifyDrafts()
        draft
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
            return findById(type, id)
        }
        if (!name) {
            return null
        }

        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'name', name.toString()
        }

        if (classifications) {
            T result = getLatestFromCriteria(classificationService.classified(criteria, classifications))

            if (result) {
                return result
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

        // ok unclassified, return it
        return result
    }

    protected <T extends CatalogueElement> T findById(Class<T> type, Object id) {
        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'modelCatalogueId', id.toString()
        }
        return getLatestFromCriteria(criteria)
    }

    private <T extends CatalogueElement> T getLatestFromCriteria(DetachedCriteria<T> criteria, boolean unclassifiedOnly = false) {
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

}
