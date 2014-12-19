package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.RelationshipType

class RelationshipBuilder {

    final CatalogueBuilderContext context
    final CatalogueElementProxyRepository repository
    final RelationshipType type

    RelationshipBuilder(CatalogueBuilderContext context, CatalogueElementProxyRepository repository, String type) {
        this.repository = repository
        this.context = context
        this.type = RelationshipType.readByName(type)
    }

    void to(String classification, String name) {
        to repository.createAbstractionByClassificationAndName(type.sourceClass, classification, name)
    }

    void to(String name) {
        context.withContextElement(Classification) {
            to repository.createAbstractionByClassificationAndName(type.sourceClass, it.name, name)
        } or {
            to repository.createAbstractionByName(type.sourceClass, name)
        }
    }

    public <T extends CatalogueElement> void to(CatalogueElementProxy<T> element) {
        context.withContextElement(type.sourceClass) {
            return it.addToPendingRelationships(new RelationshipProxy(type.name, it, element))
        } or {
            throw new IllegalStateException("There is no contextual element available of type $type.sourceClass")
        }
    }

    void from(String classification, String name) {
        from repository.createAbstractionByClassificationAndName(type.destinationClass, classification, name)
    }

    void from(String name) {
        context.withContextElement(Classification) {
            from repository.createAbstractionByClassificationAndName(type.destinationClass, it.name, name)
        } or {
            from repository.createAbstractionByName(type.destinationClass, name)
        }
    }

    public <T extends CatalogueElement> void from(CatalogueElementProxy<T> element) {
        context.withContextElement(type.destinationClass) {
            return it.addToPendingRelationships(new RelationshipProxy(type.name, element, it))
        } or {
            throw new IllegalStateException("There is no contextual element available of type $type.destinationClass")
        }
    }

}
