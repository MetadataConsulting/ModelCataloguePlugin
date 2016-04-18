package org.modelcatalogue.core.ddl

import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import grails.util.Holders
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.util.DataModelFilter

/**
 * Simple data definition language. Designed to be usually used in the tests.
 */
class DataDefinitionLanguage {

    static void with(DataModel dataModel, @DelegatesTo(DataDefinitionLanguage) Closure closure) {
        new DataDefinitionLanguage(dataModel: dataModel).with closure
    }
    static void with(String dataModel, @DelegatesTo(DataDefinitionLanguage) Closure closure) {
        new DataDefinitionLanguage(dataModel: find(DataModel, dataModel, null)).with closure
    }

    private DataModel dataModel

    UpdateDefinition update(String propertyOrExtName) {
        return new UpdateDefinition(this, propertyOrExtName)
    }

    public <T extends CatalogueElement> CreateDefinition<T> create(Class<T> domain) {
        return new CreateDefinition<T>(this, domain)
    }

    CreateDraftDefinition create(DraftKeyword ignored) {
        return new CreateDraftDefinition(this)
    }

    void finalize(String name, String version = null, String revisionNotes = null) {
        DataModel dataModel = find(DataModel, name)

        if (!dataModel) {
            throw new IllegalArgumentException("No data model called $name exist!")
        }

        Holders.applicationContext.getBean(ElementService).finalizeDataModel(dataModel, version ?: dataModel.semanticVersion, revisionNotes ?: 'TEST', true)
    }

    void deprecate(String name) {
        Holders.applicationContext.getBean(ElementService).archive(find(CatalogueElement, name), true)
    }

    static DraftKeyword getDraft() {
        return DraftKeyword.INSTANCE
    }

    protected DataModel getDataModel() {
        return dataModel
    }

    protected <T extends CatalogueElement> T find(Class<T> domain, String name) {
        find domain, name, dataModel
    }

    protected static <T extends CatalogueElement> T find(Class<T> domain, String name, DataModel dataModel) {
        DetachedCriteria<T> criteria = new DetachedCriteria<T>(domain).build {
            eq 'name', name
        }

        if (domain != dataModel && dataModel) {
            criteria = DataModelService.classified(criteria, DataModelFilter.includes(dataModel))
        }

        List<T> elements = criteria.list(sort: 'versionNumber', order: 'desc')
        T element = elements ? elements[0] : null
        if (!element && domain == CatalogueElement) {
            criteria = new DetachedCriteria<T>(domain).build {
                eq 'name', name
            }
            elements = criteria.list(sort: 'versionNumber', order: 'desc')
            element = elements ? elements[0] : null
            if (!element.instanceOf(DataModel)) {
                element = null
            }
        }
        if (!element) {
            throw new IllegalArgumentException("${GrailsNameUtils.getNaturalName(domain.simpleName)} '$name' not found!")
        }
        return element
    }
}
