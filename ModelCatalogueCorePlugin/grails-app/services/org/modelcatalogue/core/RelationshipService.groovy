package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.RelationshipDirection

class RelationshipService {

    static transactional = true

    ListWithTotal<Relationship> getRelationships(Map params, RelationshipDirection direction, CatalogueElement element, RelationshipType type = null) {
        DetachedCriteria<Relationship> criteria = direction.composeWhere(element, type)
        new ListAndCount(list: criteria.list([sort: 'id'] << params), count: criteria.count())
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

        //specific rules when creating links to and from published elements
        if(source.instanceOf(PublishedElement) || destination.instanceOf(PublishedElement)){
            if(relationshipType.name=="containment" && source.status!=PublishedElementStatus.DRAFT && source.status!=PublishedElementStatus.UPDATED){
                relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedModel.add', [source.status.toString()] as Object[], "Cannot add new data elements to {0} models. Please create a new version before adding any additional elements")
                return relationshipInstance
            }
        }

        source?.addToOutgoingRelationships(relationshipInstance)
        destination?.addToIncomingRelationships(relationshipInstance)
        relationshipInstance.save(flush: true)
        relationshipInstance
    }


    Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {

        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)

            //specific rules when creating links to and from published elements
            if(source.instanceOf(PublishedElement) || destination.instanceOf(PublishedElement)){
                if(relationshipType.name=="containment" && source.status!=PublishedElementStatus.DRAFT && source.status!=PublishedElementStatus.UPDATED){
                    relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedModel.remove', [source.status.toString()] as Object[], "Cannot add removed data elements from {0} models. Please create a new version before removing any additional elements")
                    return relationshipInstance
                }
            }

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
