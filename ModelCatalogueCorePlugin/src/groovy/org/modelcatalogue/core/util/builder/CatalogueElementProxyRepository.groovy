package org.modelcatalogue.core.util.builder

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.MeasurementUnit

class CatalogueElementProxyRepository {

    private Set<Class> HAS_UNIQUE_NAMES = [MeasurementUnit, Classification]
    private static final Map LATEST = [sort: 'versionNumber', order: 'asc', max: 1]

    private final ClassificationService classificationService

    Set<Class> unclassifiedQueriesFor = []
    Set<CatalogueElementProxy> pendingProxies = []

    CatalogueElementProxyRepository(ClassificationService classificationService) {
        this.classificationService = classificationService
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


    public <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionById(Class<T> domain, String name, Object id) {
        return new CatalogueElementById<T>(this, domain, name, id)
    }

    public <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByClassificationAndName(Class<T> domain, String classificationName, String name) {
        return new CatalogueElementByNameAndClassification<T>(this, domain, classificationName, name)
    }

    public <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByName(Class<T> domain, String name) {
        return new CatalogueElementByName<T>(this, domain, name)
    }


    public static <T extends CatalogueElement> T save(T element) {
        element.save(failOnError: true, flush: true)
    }

    protected  <T extends CatalogueElement> T tryFind(Class<T> type, Object classificationName, Object name, Object id) {
        Classification classification = tryFindUnclassified(Classification, classificationName, id)
        if (!classification) {
            throw new IllegalArgumentException("Requested classification ${classificationName} is not present in the catalogue!")
        }
        tryFindWithClassification(type, classification, name, id)
    }

    protected <T extends CatalogueElement> T tryFindUnclassified(Class<T> type, Object name, Object id) {
        tryFindWithClassification(type, null, name, id)
    }

    protected <T extends CatalogueElement> T tryFindWithClassification(Class<T> type, Classification classification, Object name, Object id) {
        if (id) {
            return findById(type, id)
        }
        if (!name) {
            return null
        }

        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'name', name.toString()
        }

        if (classification) {
            T result = getLatestFromCriteria(classificationService.classified(criteria, [classification]))

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
