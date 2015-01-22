package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.RelationshipType

class RelationshipBuilder {

    final CatalogueBuilderContext context
    final CatalogueElementProxyRepository repository
    final RelationshipType type

    private Class sourceClassHint
    private Class destinationClassHint

    RelationshipBuilder(CatalogueBuilderContext context, CatalogueElementProxyRepository repository, String type) {
        this.repository = repository
        this.context = context
        this.type = RelationshipType.readByName(type)
    }

    void to(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        to repository.createAbstractionByClassificationAndName(getDestinationHintOrClass(), classification, name), extensions
    }

    void to(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(Classification) {
            to repository.createAbstractionByClassificationAndName(getDestinationHintOrClass(), it.name, name), extensions
        } or {
            to repository.createAbstractionByName(getDestinationHintOrClass(), name), extensions
        }
    }


    public <T extends CatalogueElement> void to(CatalogueElementProxy<T> element, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(getSourceHintOrClass()) {
            return it.addToPendingRelationships(new RelationshipProxy(type.name, it, element, extensions))
        } or {
            throw new IllegalStateException("There is no contextual element available of type ${getSourceHintOrClass()}")
        }
    }

    void from(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        from repository.createAbstractionByClassificationAndName(getSourceHintOrClass(), classification, name), extensions
    }

    void from(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(Classification) {
            from repository.createAbstractionByClassificationAndName(getSourceHintOrClass(), it.name, name), extensions
        } or {
            from repository.createAbstractionByName(getSourceHintOrClass(), name), extensions
        }
    }

    public <T extends CatalogueElement> void from(CatalogueElementProxy<T> element, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(getDestinationHintOrClass()) {
            return it.addToPendingRelationships(new RelationshipProxy(type.name, element, it, extensions))
        } or {
            throw new IllegalStateException("There is no contextual element available of type ${getDestinationHintOrClass()}")
        }
    }


    RelationshipBuilder to(Class domain) {
        destinationClassHint = domain == EnumeratedType ? DataType : domain
        this
    }

    RelationshipBuilder from(Class domain) {
        sourceClassHint = domain == EnumeratedType ? DataType : domain
        this
    }

    void called(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        if (sourceClassHint) {
            from name, extensions
        } else if (destinationClassHint) {
            to name, extensions
        } else {
            throw new IllegalStateException("Please set the domain hint first using to(Class) or from(Class) methods or use to(String) or from(String) methods directly")
        }
    }

    void called(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        if (sourceClassHint) {
            from classification, name, extensions
        } else if (destinationClassHint) {
            to classification, name, extensions
        } else {
            throw new IllegalStateException("Please set the domain hint first using to(Class) or from(Class) methods or use to(String, String]) or from(String , String) methods directly")
        }
    }


    private Class getSourceHintOrClass(){
        sourceClassHint ?: type.sourceClass
    }

    private Class getDestinationHintOrClass() {
        destinationClassHint ?: type.destinationClass
    }

}
