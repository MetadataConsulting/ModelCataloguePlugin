package org.modelcatalogue.core

import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.RelationshipDirection

class RelationshipService {

    static final long INDEX_STEP = 1000

    static transactional = true

    def modelCatalogueSecurityService

    ListWithTotal<Relationship> getRelationships(Map params, RelationshipDirection direction, CatalogueElement element, RelationshipType type = null) {
        Lists.fromCriteria(params, direction.composeWhere(element, type, getClassifications(modelCatalogueSecurityService.currentUser)))
    }

    Relationship link(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, Classification classification, boolean archived = false, boolean ignoreRules = false, Long indexHint = null) {
        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipTypeAndClassification(source, destination, relationshipType, classification)
            if (relationshipInstance) {
                if (indexHint == null || indexHint == relationshipInstance.outgoingIndex) {
                    return relationshipInstance
                }
                relationshipInstance.outgoingIndex = indexHint
                return relationshipInstance.save(flush: true)
            }
        }

        Relationship relationshipInstance = new Relationship(
                source: source?.id ? source : null,
                destination: destination?.id ? destination : null,
                relationshipType: relationshipType?.id ? relationshipType : null,
                classification: classification?.id ? classification : null,
                archived: archived,
                outgoingIndex: indexHint ?: System.currentTimeMillis()
        )

        //specific rules when creating links to and from published elements
        // TODO: it doesn't seem to be good idea place it here. would be nice if you can put it somewhere where it is more pluggable
        if(!ignoreRules) {
            if (relationshipType.name == "containment" && !(source.status in [ElementStatus.DRAFT, ElementStatus.UPDATED, ElementStatus.PENDING])) {
                relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedModel.add', [source.status.toString()] as Object[], "Cannot add new data elements to {0} models. Please create a new version before adding any additional elements")
                return relationshipInstance
            }

            if (relationshipType.name == "instantiation" && !(source.status in [ElementStatus.DRAFT, ElementStatus.UPDATED, ElementStatus.PENDING])) {
                relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedModel.add', [source.status.toString()] as Object[], "Cannot add new value domain elements to {0} data element. Please create a new version before adding any additional values")
                return relationshipInstance
            }
        }

        relationshipInstance.validate()

        if (relationshipInstance.hasErrors()) {
            return relationshipInstance
        }

        if (relationshipType.sortable) {
            relationshipInstance.outgoingIndex = (getMaxOutgoingIndex(source, relationshipType) ?: 0) + INDEX_STEP
        }

        relationshipInstance.save(flush: true)
        source?.addToOutgoingRelationships(relationshipInstance)
        destination?.addToIncomingRelationships(relationshipInstance)
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

            // specific rules when creating links to and from published elements
            // XXX: this should be in the relationship type!
            if(!ignoreRules) {
                if (relationshipType.name == "containment" && source.status != ElementStatus.DRAFT && source.status != ElementStatus.UPDATED && source.status != ElementStatus.DEPRECATED) {
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
            select r.source.name, r.source.id
            from Relationship as r
            where r.relationshipType = :classification
            and r.destination.id = :elementId
            order by r.source.name
        """, [classification: classification, elementId: element.id]).collect {
            [name: it[0], id: it[1], elementType: Classification.name, link:  "/classification/${it[1]}"]
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

    Relationship moveAfter(Relationship relationship, Relationship other) {
        if (!relationship || relationship.hasErrors() || !relationship.relationshipType.sortable) {
            return relationship
        }

        if (!other) {
            relationship.outgoingIndex = getMinOutgoingIndex(relationship.source, relationship.relationshipType) - INDEX_STEP
            return relationship.save()
        }

        if (other.outgoingIndex == null) {
            return moveAfterWithRearrange(relationship, other)
        }

        if (relationship.source != other.source) {
            relationship.errors.reject('relationship.moveAfter.different.source', "Cannot reorder as the sources are different ($relationship.source, $other.source)")
            return relationship
        }

        Long nextIndex = getMinOutgoingIndexAfter(relationship.source, relationship.relationshipType, other.outgoingIndex)

        if (nextIndex == null) {
            relationship.outgoingIndex = other.outgoingIndex + INDEX_STEP
            return relationship.save()
        }

        if (nextIndex - other.outgoingIndex > 1) {
            relationship.outgoingIndex = other.outgoingIndex + Math.round((nextIndex.doubleValue() - other.outgoingIndex) / 2)
            return relationship.save()
        }

        moveAfterWithRearrange(relationship, other)
    }

    private static Relationship moveAfterWithRearrange(Relationship relationship, Relationship other) {
        List<Relationship> relationships  = RelationshipDirection.OUTGOING.composeWhere(relationship.source, relationship.relationshipType, []).list()
        int correction = 0
        relationships.eachWithIndex { Relationship entry, Integer i ->
            if (entry == relationship) {
                correction = -1
                return
            }
            entry.outgoingIndex = (i + correction ) * INDEX_STEP

            if (entry == other) {
                correction++
                relationship.outgoingIndex = (i + correction) * INDEX_STEP
                relationship.save(failOnError: true)
            }

            entry.save(failOnError: true)
        }
        relationship
    }

    private static Long getMaxOutgoingIndex(CatalogueElement source, RelationshipType relationshipType) {
        Relationship.executeQuery("""
            select max(r.outgoingIndex) from Relationship r
            where r.source = :source
            and r.relationshipType = :type
        """, [source: source, type: relationshipType])[0] as Long
    }

    private static Long getMinOutgoingIndex(CatalogueElement source, RelationshipType relationshipType) {
        Relationship.executeQuery("""
            select min(r.outgoingIndex) from Relationship r
            where r.source = :source
            and r.relationshipType = :type
        """, [source: source, type: relationshipType])[0] as Long
    }

    private static Long getMinOutgoingIndexAfter(CatalogueElement source, RelationshipType relationshipType, Long current) {
        Relationship.executeQuery("""
            select min(r.outgoingIndex) from Relationship r
            where r.source = :source
            and r.relationshipType = :type
            and r.outgoingIndex > :current
        """, [source: source, type: relationshipType, current: current])[0] as Long
    }


}
