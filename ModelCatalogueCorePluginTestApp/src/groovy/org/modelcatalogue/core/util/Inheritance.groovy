package org.modelcatalogue.core.util

import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

class Inheritance {

    static void withAllParents(CatalogueElement element, Set<CatalogueElement> processed = new HashSet<CatalogueElement>([element]),  @DelegatesTo(CatalogueElement) Closure closure) {
        if (element == null) {
            return
        }
        if (!element.countOutgoingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getOutgoingRelationshipsByType(RelationshipType.baseType))) {
            if (relationship.destination in processed) {
                continue
            }
            processed << relationship.destination
            withElementAndRelationship(closure, relationship.destination, relationship)
            withAllParents(relationship.destination, processed, closure)
        }
    }

    static void withAllChildren(CatalogueElement element, Set<CatalogueElement> processed = new HashSet<CatalogueElement>([element]),  @DelegatesTo(CatalogueElement) Closure closure) {
        if (element == null) {
            return
        }
        if (!element.countIncomingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getIncomingRelationshipsByType(RelationshipType.baseType))) {
            if (relationship.source in processed) {
                continue
            }
            processed << relationship.source
            withElementAndRelationship(closure, relationship.source, relationship)
            withAllChildren(relationship.source, processed, closure)
        }
    }

    static void withParents(CatalogueElement element, @DelegatesTo(CatalogueElement) Closure closure) {
        if (element == null) {
            return
        }
        if (!element.countOutgoingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getOutgoingRelationshipsByType(RelationshipType.baseType))) {
            withElementAndRelationship(closure, relationship.destination, relationship)
        }
    }

    static void withChildren(CatalogueElement element, @DelegatesTo(CatalogueElement) Closure closure) {
        if (element == null) {
            return
        }
        if (!element.countIncomingRelationshipsByType(RelationshipType.baseType)) {
            return
        }
        for (Relationship relationship in new ArrayList<Relationship>(element.getIncomingRelationshipsByType(RelationshipType.baseType))) {
            withElementAndRelationship(closure, relationship.source, relationship)
        }
    }

    private static void withElementAndRelationship(Closure closure, CatalogueElement element, Relationship relationship) {
        if (element == null || relationship == null) {
            return
        }
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
