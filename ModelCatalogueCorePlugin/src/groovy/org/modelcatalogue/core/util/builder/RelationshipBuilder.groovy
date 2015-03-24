package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.*

/**
 * RelationshipBuilder is supplementary class to CatalogueBuilder handling part of the DSL dealing with creating
 * relationships.
 */
class RelationshipBuilder {

    /**
     * Current context of the parent catalogue builder.
     */
    final CatalogueBuilderContext context

    /**
     * Current repository of the parent catalogue builder.
     */
    final CatalogueElementProxyRepository repository

    /**
     * Type of the relationship created.
     */
    final RelationshipType type

    /**
     * Hint for the source class to distinguish between elements of the same name but with different type.
     */
    private Class sourceClassHint

    /**
     * Hint for the source class to distinguish between elements of the same name but with different type.
     */
    private Class destinationClassHint

    /**
     * Creates new relationship builder with given context, repository and relationship type.
     *
     * @param context current context of the catalogue builder
     * @param repository current repository of the catalogue builder
     * @param type type of the relationship created
     */
    RelationshipBuilder(CatalogueBuilderContext context, CatalogueElementProxyRepository repository, String type) {
        this.repository = repository
        this.context = context
        this.type = RelationshipType.readByName(type)
    }

    /**
     * Specifies the destination of the relationship created by given classification and name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param classification classification of the destination
     * @param name name of the destination
     * @param extensions closure defining the metadata
     */
    void to(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        to repository.createProxy(getDestinationHintOrClass(), [classification: classification, name: name]), extensions
    }

    /**
     * Specifies the destination of the relationship created by given name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param name name of the destination
     * @param extensions closure defining the metadata
     */
    void to(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(Classification) {
            to repository.createProxy(getDestinationHintOrClass(), [classification: it.name, name: name]), extensions
        } or {
            to repository.createProxy(getDestinationHintOrClass(), [name: name]), extensions
        }
    }


    /**
     * Specifies the destination of the relationship created by given proxy. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param proxy proxy of the destination
     * @param extensions closure defining the metadata
     */
    public <T extends CatalogueElement> void to(CatalogueElementProxy<T> element, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(getSourceHintOrClass()) {
            return it.addToPendingRelationships(new RelationshipProxy(type.name, it, element, extensions))
        } or {
            throw new IllegalStateException("There is no contextual element available of type ${getSourceHintOrClass()}")
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
    void from(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        from repository.createProxy(getSourceHintOrClass(), [classification: classification, name: name]), extensions
    }

    /**
     * Specifies the source of the relationship created by given classification and name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param classification classification of the source
     * @param name name of the source
     * @param extensions closure defining the metadata
     */
    void from(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(Classification) {
            from repository.createProxy(getSourceHintOrClass(), [classification: it.name, name: name]), extensions
        } or {
            from repository.createProxy(getSourceHintOrClass(), [name: name]), extensions
        }
    }
    /**
     * Specifies the source of the relationship created by given proxy. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param proxy proxy of the source
     * @param extensions closure defining the metadata
     */
    public <T extends CatalogueElement> void from(CatalogueElementProxy<T> element, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(getDestinationHintOrClass()) {
            return it.addToPendingRelationships(new RelationshipProxy(type.name, element, it, extensions))
        } or {
            throw new IllegalStateException("There is no contextual element available of type ${getDestinationHintOrClass()}")
        }
    }

    /**
     * Specifies type hint for the destination. Continue with #called(String, String) or #called(String) to
     * create the relationship.
     * @param domain expected type of the destination
     * @return self
     */
    RelationshipBuilder to(Class domain) {
        destinationClassHint = domain == EnumeratedType ? DataType : domain
        this
    }

    /**
     * Specifies type hint for the source. Continue with #called(String, String) or #called(String) to
     * create the relationship.
     * @param domain expected type of the source
     * @return self
     */
    RelationshipBuilder from(Class domain) {
        sourceClassHint = domain == EnumeratedType ? DataType : domain
        this
    }

    /**
     * Specifies the source or destination of the relationship created by given name. The metadata for this
     * relationship can be specified inside the extensions closure. #from(Class) or #to(Class) must be called before)
     * calling this method.
     *
     * @param name name of the source or destination
     * @param extensions closure defining the metadata
     */
    void called(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        if (sourceClassHint) {
            from name, extensions
        } else if (destinationClassHint) {
            to name, extensions
        } else {
            throw new IllegalStateException("Please set the domain hint first using to(Class) or from(Class) methods or use to(String) or from(String) methods directly")
        }
    }

    /**
     * Specifies the source or destination of the relationship created by given name. The metadata for this
     * relationship can be specified inside the extensions closure. #from(Class) or #to(Class) must be called before)
     * calling this method.
     * @param classification classification of the source or destination
     * @param name name of the source or destination
     * @param extensions closure defining the metadata
     */
    void called(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        if (sourceClassHint) {
            from classification, name, extensions
        } else if (destinationClassHint) {
            to classification, name, extensions
        } else {
            throw new IllegalStateException("Please set the domain hint first using to(Class) or from(Class) methods or use to(String, String]) or from(String , String) methods directly")
        }
    }

    /**
     * @return hint for the source type - either user supplied or the source class of the relationship type.
     */
    private Class getSourceHintOrClass(){
        sourceClassHint ?: type.sourceClass
    }

    /*
     * @return hint for the destination type - either user supplied or the destination class of the relationship type.
     */
    private Class getDestinationHintOrClass() {
        destinationClassHint ?: type.destinationClass
    }

}
