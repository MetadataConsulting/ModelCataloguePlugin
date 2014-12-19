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

    void to(String classification, String name) {
        to repository.createAbstractionByClassificationAndName(getDestinationHintOrClass(), classification, name)
    }

    void to(String name) {
        context.withContextElement(Classification) {
            to repository.createAbstractionByClassificationAndName(getDestinationHintOrClass(), it.name, name)
        } or {
            to repository.createAbstractionByName(getDestinationHintOrClass(), name)
        }
    }


    public <T extends CatalogueElement> void to(CatalogueElementProxy<T> element) {
        context.withContextElement(getSourceHintOrClass()) {
            return it.addToPendingRelationships(new RelationshipProxy(type.name, it, element))
        } or {
            throw new IllegalStateException("There is no contextual element available of type ${getSourceHintOrClass()}")
        }
    }

    void from(String classification, String name) {
        from repository.createAbstractionByClassificationAndName(getSourceHintOrClass(), classification, name)
    }

    void from(String name) {
        context.withContextElement(Classification) {
            from repository.createAbstractionByClassificationAndName(getSourceHintOrClass(), it.name, name)
        } or {
            from repository.createAbstractionByName(getSourceHintOrClass(), name)
        }
    }

    public <T extends CatalogueElement> void from(CatalogueElementProxy<T> element) {
        context.withContextElement(getDestinationHintOrClass()) {
            return it.addToPendingRelationships(new RelationshipProxy(type.name, element, it))
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

    void called(String name) {
        if (sourceClassHint) {
            from name
        } else if (destinationClassHint) {
            to name
        } else {
            throw new IllegalStateException("Please set the domain hint first using to(Class) or from(Class) methods or use to(String) or from(String) methods directly")
        }
    }

    void called(String classification, String name) {
        if (sourceClassHint) {
            from classification, name
        } else if (destinationClassHint) {
            to classification, name
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
