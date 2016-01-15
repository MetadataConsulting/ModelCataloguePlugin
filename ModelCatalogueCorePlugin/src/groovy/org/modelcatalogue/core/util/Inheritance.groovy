package org.modelcatalogue.core.util

import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

class Inheritance {

    static void withAllChildren(CatalogueElement element, Set<CatalogueElement> processed = new HashSet<CatalogueElement>([element]),  @DelegatesTo(CatalogueElement) Closure closure) {
        if (!element.countOutgoingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getOutgoingRelationshipsByType(RelationshipType.baseType))) {
            if (relationship.destination in processed) {
                continue
            }
            processed << relationship.destination
            relationship.destination.with closure
            withAllChildren(relationship.destination, processed, closure)
        }
    }

    static void withAllParents(CatalogueElement element, Set<CatalogueElement> processed = new HashSet<CatalogueElement>([element]),  @DelegatesTo(CatalogueElement) Closure closure) {
        if (!element.countIncomingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getIncomingRelationshipsByType(RelationshipType.baseType))) {
            if (relationship.source in processed) {
                continue
            }
            processed << relationship.source
            relationship.source.with closure
            withAllParents(relationship.source, processed, closure)
        }
    }

    static void withChildren(CatalogueElement element, @DelegatesTo(CatalogueElement) Closure closure) {
        if (!element.countOutgoingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getOutgoingRelationshipsByType(RelationshipType.baseType))) {
            relationship.destination.with closure
        }
    }

    static void withParents(CatalogueElement element, @DelegatesTo(CatalogueElement) Closure closure) {
        if (!element.countIncomingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getIncomingRelationshipsByType(RelationshipType.baseType))) {
            relationship.source.with closure
        }
    }

}
