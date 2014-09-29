package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.RelationshipDirection

class RelationshipService {

    static transactional = true

    ListWithTotal<Relationship> getRelationships(Map params, RelationshipDirection direction, CatalogueElement element, RelationshipType type = null) {
        Lists.fromCriteria([sort: 'id'] << params, direction.composeWhere(element, type))
    }


    Relationship link(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, boolean archived = false, boolean ignoreRules = false) {
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
        // TODO: it doesn't seem to be good idea place it here. would be nice if you can put it somewhere where it is more pluggable
        if(!ignoreRules) {
            if (source.instanceOf(PublishedElement) || destination.instanceOf(PublishedElement)) {
                if (relationshipType.name == "containment" && !(source.status in [PublishedElementStatus.DRAFT, PublishedElementStatus.UPDATED, PublishedElementStatus.PENDING])) {
                    relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedModel.add', [source.status.toString()] as Object[], "Cannot add new data elements to {0} models. Please create a new version before adding any additional elements")
                    return relationshipInstance
                }

                if (relationshipType.name == "instantiation" && !(source.status in [PublishedElementStatus.DRAFT, PublishedElementStatus.UPDATED, PublishedElementStatus.PENDING])) {
                    relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedModel.add', [source.status.toString()] as Object[], "Cannot add new value domain elements to {0} data element. Please create a new version before adding any additional values")
                    return relationshipInstance
                }

            }
        }

        relationshipInstance.validate()

        if (relationshipInstance.hasErrors()) {
            return relationshipInstance
        }

        source?.addToOutgoingRelationships(relationshipInstance)
        destination?.addToIncomingRelationships(relationshipInstance)
        relationshipInstance.save(flush: true)
        relationshipInstance
    }


    Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, boolean ignoreRules = false) {

        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)

            // specific rules when creating links to and from published elements
            // XXX: this should be in the relationship type!
            if(!ignoreRules) {
                if (source.instanceOf(PublishedElement) || destination.instanceOf(PublishedElement)) {
                    if (relationshipType.name == "containment" && source.status != PublishedElementStatus.DRAFT && source.status != PublishedElementStatus.UPDATED && source.status != PublishedElementStatus.ARCHIVED) {
                        relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedDataElement.remove', [source.status.toString()] as Object[], "Cannot add removed data elements from {0} models. Please create a new version of the MODEL before removing any additional elements or archive the element first if you want to delete it.")
                        return relationshipInstance
                    }
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
