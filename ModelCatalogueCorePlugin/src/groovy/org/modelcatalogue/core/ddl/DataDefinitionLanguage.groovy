package org.modelcatalogue.core.ddl

import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import grails.util.Holders
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.util.ClassificationFilter

/**
 * Simple data definition language. Designed to be usually used in the tests.
 */
class DataDefinitionLanguage {

    static void with(Classification classification, @DelegatesTo(DataDefinitionLanguage) Closure closure) {
        new DataDefinitionLanguage(classification: classification).with closure
    }
    static void with(String classification, @DelegatesTo(DataDefinitionLanguage) Closure closure) {
        new DataDefinitionLanguage(classification: find(Classification, classification, null)).with closure
    }

    private Classification classification

    UpdateDefinition update(String propertyOrExtName) {
        return new UpdateDefinition(this, propertyOrExtName)
    }

    public <T extends CatalogueElement> CreateDefinition<T> create(Class<T> domain) {
        return new CreateDefinition<T>(this, domain)
    }

    CreateDraftDefinition create(DraftKeyword ignored) {
        return new CreateDraftDefinition(this)
    }

    void finalize(String name) {
        Holders.applicationContext.getBean(ElementService).finalizeElement(find(CatalogueElement, name))
    }

    void deprecate(String name) {
        Holders.applicationContext.getBean(ElementService).archive(find(CatalogueElement, name), true)
    }

    static DraftKeyword getDraft() {
        return DraftKeyword.INSTANCE
    }

    protected Classification getClassification() {
        return classification
    }

    protected <T extends CatalogueElement> T find(Class<T> domain, String name) {
        find domain, name, classification
    }

    protected static <T extends CatalogueElement> T find(Class<T> domain, String name, Classification classification) {
        DetachedCriteria<T> criteria = new DetachedCriteria<T>(domain).build {
            eq 'name', name
        }

        if (domain != classification && classification) {
            criteria = ClassificationService.classified(criteria, ClassificationFilter.includes(classification))
        }

        List<T> elements = criteria.list(sort: 'versionNumber', order: 'desc')
        T element = elements ? elements[0] : null
        if (!element && domain == CatalogueElement) {
            criteria = new DetachedCriteria<T>(domain).build {
                eq 'name', name
            }
            elements = criteria.list(sort: 'versionNumber', order: 'desc')
            element = elements ? elements[0] : null
            if (!element?.instanceOf(Classification)) {
                element = null
            }
        }
        if (!element) {
            throw new IllegalArgumentException("${GrailsNameUtils.getNaturalName(domain.simpleName)} '$name' not found!")
        }
        return element
    }
}
