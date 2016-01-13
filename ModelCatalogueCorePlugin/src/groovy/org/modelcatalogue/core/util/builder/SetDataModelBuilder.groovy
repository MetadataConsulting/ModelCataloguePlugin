package org.modelcatalogue.core.util.builder

import org.modelcatalogue.builder.api.BuilderKeyword
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.builder.api.RelationshipBuilder
import org.modelcatalogue.builder.api.RelationshipConfiguration
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.CatalogueElement

/**
 * RelationshipBuilder is supplementary class to CatalogueBuilder handling part of the DSL dealing with creating
 * relationships.
 */
class SetDataModelBuilder implements RelationshipBuilder {

    /**
     * Current context of the parent catalogue builder.
     */
    final CatalogueBuilderContext context


    /**
     * Current repository of the parent catalogue builder.
     */
    final CatalogueElementProxyRepository repository

    /**
     * Creates new relationship builder with given context, repository and relationship type.
     *
     * @param context current context of the catalogue builder
     * @param type type of the relationship created
     */
    SetDataModelBuilder(CatalogueBuilderContext context, CatalogueElementProxyRepository repository) {
        this.context = context
        this.repository = repository
    }

    void to(String classification, String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        throw new UnsupportedOperationException("Data model can only be set using 'from' with the 'classification' or 'declaration' relationship type")
    }

    /**
     * Specifies the destination of the relationship created by given name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param name name of the destination
     * @param extensions closure defining the metadata
     */
    void to(String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        throw new UnsupportedOperationException("Data model can only be set using 'from' with the 'classification' or 'declaration' relationship type")
    }


    /**
     * Specifies the destination of the relationship created by given proxy. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param proxy proxy of the destination
     * @param extensions closure defining the metadata
     */
    void to(CatalogueElementProxy element, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        throw new UnsupportedOperationException("Data model can only be set using 'from' with the 'classification' or 'declaration' relationship type")
    }

    /**
     * Specifies the source of the relationship created by given classification and name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param classification classification of the source
     * @param name name of the source
     * @param extensions closure defining the metadata
     */
    void from(String classification, String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        context.withContextElement(org.modelcatalogue.core.CatalogueElement) {
            it.setParameter('dataModel', repository.createProxy(DataModel, [name: name, dataModel: repository.createProxy(DataModel, [name: classification])]))
        }
    }

    /**
     * Specifies the source of the relationship created by given classification and name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param classification classification of the source
     * @param name name of the source
     * @param extensions closure defining the metadata
     */
    @Override
    void to(CatalogueElement element, @DelegatesTo(RelationshipConfiguration) Closure extensions) {
        throw new UnsupportedOperationException("Data model can only be set using 'from' with the 'classification' or 'declaration' relationship type")
    }

    @Override
    void to(CatalogueElement element) {
        throw new UnsupportedOperationException("Data model can only be set using 'from' with the 'classification' or 'declaration' relationship type")
    }

    @Override
    void from(CatalogueElement element, @DelegatesTo(RelationshipConfiguration) Closure extensions) {
        from element.name
    }

    @Override
    void from(CatalogueElement element) {
        from element.name
    }

    void from(String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        context.withContextElement(org.modelcatalogue.core.CatalogueElement) {
            it.setParameter('dataModel', repository.createProxy(DataModel, [name: name]))
        }
    }
    public void from(CatalogueElementProxy element, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        context.withContextElement(org.modelcatalogue.core.CatalogueElement) {
            it.setParameter('dataModel', element)
        }
    }

    SetDataModelBuilder to(BuilderKeyword type) {
        throw new UnsupportedOperationException("Data model can only be set using 'from' with the 'classification' or 'declaration' relationship type")
    }

    SetDataModelBuilder from(BuilderKeyword type) {
        if (type != ModelCatalogueTypes.DATA_MODEL) {
            throw new IllegalArgumentException("Unsupported keyword: $type, only dataModel supported")
        }
        this
    }

    void called(String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        from name, extensions
    }

    void called(String classification, String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        throw new UnsupportedOperationException("Only one parameter - data model - expected")
    }
}
