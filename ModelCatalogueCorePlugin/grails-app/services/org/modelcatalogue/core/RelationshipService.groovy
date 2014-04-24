package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.RelationshipDirection

class RelationshipService {

    static transactional = true

    ListAndCount getRelationships(Map params, RelationshipDirection direction, CatalogueElement element, RelationshipType type = null) {
        DetachedCriteria<Relationship> criteria = direction.composeWhere(element, type)
        new ListAndCount(list: criteria.list(params), count: criteria.count(params))
    }


    Relationship link(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, boolean archived = false) {
        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)
            if (relationshipInstance) { return relationshipInstance }
        }
        Relationship relationshipInstance = new Relationship(
                source: source?.id ? source : null,
                destination: destination?.id ? destination : null,
                relationshipType: relationshipType?.id ? relationshipType : null,
                archived: archived
        )
        source?.addToOutgoingRelationships(relationshipInstance)
        destination?.addToIncomingRelationships(relationshipInstance)
        relationshipInstance.save(flush: true)
        relationshipInstance
    }


    Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)
            if (relationshipInstance && source && destination) {
                destination?.removeFromIncomingRelationships(relationshipInstance)
                source?.removeFromOutgoingRelationships(relationshipInstance)
                relationshipInstance.delete(flush: true)
                return relationshipInstance
            }
        }
        return null
    }
}
