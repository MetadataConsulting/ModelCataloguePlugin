package org.modelcatalogue.core

import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.RelationshipDirection

class RelationshipService {

    static final long INDEX_STEP = 1000

    static transactional = true

    def modelCatalogueSecurityService

    ListWithTotal<Relationship> getRelationships(Map params, RelationshipDirection direction, CatalogueElement element, RelationshipType type = null) {
        if (!params.sort) {
            params.sort = direction.sortProperty
        }
        Lists.fromCriteria(params, direction.composeWhere(element, type, getClassifications(modelCatalogueSecurityService.currentUser)))
    }

    Relationship link(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, Classification classification, boolean archived = false, boolean ignoreRules = false, boolean resetIndexes = false) {
        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipTypeAndClassification(source, destination, relationshipType, classification)

            if (!relationshipInstance && relationshipType.bidirectional) {
                relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipTypeAndClassification(destination, source, relationshipType, classification)
            }

            if (relationshipInstance) {
                if (!resetIndexes && relationshipInstance.archived == archived) {
                    return relationshipInstance
                }
                if (resetIndexes) {
                    relationshipInstance.resetIndexes()
                }
                relationshipInstance.archived = archived
                return relationshipInstance.save(flush: true)
            }
        }

        Relationship relationshipInstance = new Relationship(
                source: source?.id ? source : null,
                destination: destination?.id ? destination : null,
                relationshipType: relationshipType?.id ? relationshipType : null,
                classification: classification?.id ? classification : null,
                archived: archived
        )

        if(!ignoreRules) {
            if (relationshipType.versionSpecific && !relationshipType.system && !(source.status in [ElementStatus.DRAFT, ElementStatus.UPDATED, ElementStatus.PENDING])) {
                relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedModel.add', [source.status.toString()] as Object[], "Cannot add new elements to {0}. Please create a new version before adding any additional elements")
                return relationshipInstance
            }

        }

        relationshipInstance.validate()

        if (relationshipInstance.hasErrors()) {
            return relationshipInstance
        }

        relationshipInstance.save(flush: true)
        source?.addToOutgoingRelationships(relationshipInstance)?.save(flush: true)
        destination?.addToIncomingRelationships(relationshipInstance)?.save(flush: true)
        relationshipInstance
    }


    Relationship link(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, boolean archived = false, boolean ignoreRules = false) {
        link source, destination, relationshipType, null, archived, ignoreRules
    }

    Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, boolean ignoreRules = false) {
        unlink source, destination, relationshipType, null, ignoreRules
    }

    Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, Classification classification, boolean ignoreRules = false) {

        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipTypeAndClassification(source, destination, relationshipType, classification)

            if(!ignoreRules) {
                if (relationshipType.versionSpecific && !relationshipType.system && source.status != ElementStatus.DRAFT && source.status != ElementStatus.UPDATED && source.status != ElementStatus.DEPRECATED) {
                    relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedDataElement.remove', [source.status.toString()] as Object[], "Cannot add removed data elements from {0} models. Please create a new version of the MODEL before removing any additional elements or archive the element first if you want to delete it.")
                    return relationshipInstance
                }
            }

            if (relationshipInstance && source && destination) {
                destination?.removeFromIncomingRelationships(relationshipInstance)
                source?.removeFromOutgoingRelationships(relationshipInstance)
                relationshipInstance.classification = null
                relationshipInstance.delete(flush: true)
                return relationshipInstance
            }
        }
        return null
    }


    String getClassifiedName(CatalogueElement element) {
        if (!element) {
            return null
        }

        if (!element.id) {
            return element.name
        }

        RelationshipType classification = RelationshipType.findByName('classification')

        String classifications = Relationship.executeQuery("""
            select r.source.name
            from Relationship as r
            where r.relationshipType = :classification
            and r.destination.id = :elementId
            order by r.source.name
        """, [classification: classification, elementId: element.id]).join(', ')

        if (classifications) {
            return "${element.name} (${classifications})"
        }

        return element.name
    }

    def getClassificationsInfo(CatalogueElement element) {
        if (!element) {
            return []
        }

        if (!element.id) {
            return []
        }

        RelationshipType classification = RelationshipType.findByName('classification')

        Relationship.executeQuery("""
            select r.source.name, r.source.id, r.source.status
            from Relationship as r
            where r.relationshipType = :classification
            and r.destination.id = :elementId
            order by r.source.name
        """, [classification: classification, elementId: element.id]).collect {
            [name: it[0], id: it[1], status: "${it[2]}", elementType: Classification.name, link:  "/classification/${it[1]}"]
        }
    }

    def List<Classification> getClassifications(CatalogueElement element) {
        if (!element) {
            return []
        }

        if (!element.id) {
            return []
        }

        RelationshipType classification = RelationshipType.findByName('classification')

        Classification.executeQuery """
            select c
            from Classification as c
            join c.outgoingRelationships as rel
            where rel.relationshipType = :classification
            and rel.destination.id = :elementId
            order by c.name
        """, [classification: classification, elementId: element.id]
    }

    Relationship moveAfter(RelationshipDirection direction, CatalogueElement owner,  Relationship relationship, Relationship other) {
        if (!relationship || relationship.hasErrors()) {
            return relationship
        }

        if (!direction.getIndex(relationship)) {
            return moveAfterWithRearrange(direction, owner, relationship, other)
        }

        if (!other) {
            direction.setIndex(relationship, direction.getMinIndexAfter(owner, relationship.relationshipType, Long.MIN_VALUE) - INDEX_STEP)
            return relationship.save()
        }

        if (!direction.isOwnedBy(owner, relationship)) {
            relationship.errors.reject('relationship.moveAfter.different.owner', "Cannot reorder as the relationship $relationship.source is not owned by the element $owner")
            return relationship
        }

        if (!direction.isOwnedBy(owner, other)) {
            relationship.errors.reject('relationship.moveAfter.different.source', "Cannot reorder as the relationship $other.source is not owned by the element $owner")
            return relationship
        }

        Long nextIndex = direction.getMinIndexAfter(owner, relationship.relationshipType, direction.getIndex(other))

        if (nextIndex == null) {
            direction.setIndex(relationship, direction.getIndex(other) + INDEX_STEP)
            return relationship.save()
        }

        if (nextIndex - direction.getIndex(other) > 1) {
            direction.setIndex(relationship, direction.getIndex(other) + Math.round((nextIndex.doubleValue() - direction.getIndex(other)) / 2))
            return relationship.save()
        }

        moveAfterWithRearrange(direction, owner, relationship, other)
    }

    private static Relationship moveAfterWithRearrange(RelationshipDirection direction, CatalogueElement owner, Relationship relationship, Relationship other) {
        List<Relationship> relationships = direction.composeWhere(owner, relationship.relationshipType, []).list([sort: direction.sortProperty])
        int correction = 0
        relationships.eachWithIndex { Relationship entry, Integer i ->
            if (entry == relationship) {
                correction = -1
                return
            }
            direction.setIndex(entry, (i + correction ) * INDEX_STEP + 1)

            if (entry == other) {
                correction++
                direction.setIndex(relationship, (i + correction) * INDEX_STEP + 1)
                FriendlyErrors.failFriendlySave(relationship)
            }
            FriendlyErrors.failFriendlySave(entry)
        }
        relationship
    }


}
