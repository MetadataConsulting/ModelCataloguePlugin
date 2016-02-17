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
            withElementAndRelationship(closure, relationship.destination, relationship)
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
            withElementAndRelationship(closure, relationship.source, relationship)
            withAllParents(relationship.source, processed, closure)
        }
    }

    static void withChildren(CatalogueElement element, @DelegatesTo(CatalogueElement) Closure closure) {
        if (!element.countOutgoingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getOutgoingRelationshipsByType(RelationshipType.baseType))) {
            withElementAndRelationship(closure, relationship.destination, relationship)
        }
    }

    static void withParents(CatalogueElement element, @DelegatesTo(CatalogueElement) Closure closure) {
        if (!element.countIncomingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getIncomingRelationshipsByType(RelationshipType.baseType))) {
            withElementAndRelationship(closure, relationship.source, relationship)
        }
    }

    private static void withElementAndRelationship(Closure closure, CatalogueElement element, Relationship relationship) {
        final Closure clonedClosure = closure.clone() as Closure
        clonedClosure.resolveStrategy = Closure.DELEGATE_FIRST
        clonedClosure.setDelegate(element)

        if (clonedClosure.maximumNumberOfParameters == 2) {
            clonedClosure.call(element, relationship)
        } else {
            clonedClosure.call(element)
        }

    }

}
