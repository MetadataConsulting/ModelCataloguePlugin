package org.modelcatalogue.core.util.builder

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

@Log4j
class RelationshipProxy<T extends CatalogueElement, U extends CatalogueElement> {

    final String relationshipTypeName
    final CatalogueElementProxy<T> source
    final CatalogueElementProxy<U> destination

    // TODO: extend to be able to assign metadata as well

    RelationshipProxy(String relationshipTypeName, CatalogueElementProxy<T> source, CatalogueElementProxy<T> destination) {
        this.relationshipTypeName = relationshipTypeName
        this.source = source
        this.destination = destination
    }

    Relationship resolve() {
        RelationshipType type = RelationshipType.readByName(relationshipTypeName)
        T sourceElement = source.resolve()
        U destinationElement = destination.resolve()
        Relationship relationship = sourceElement.createLinkTo(destinationElement, type)
        if (relationship.hasErrors()) {
            log.error(relationship.errors)
            throw new IllegalStateException("Cannot create relationship of type $relationshipTypeName between $sourceElement and $destinationElement.")
        }
        relationship
    }
}
