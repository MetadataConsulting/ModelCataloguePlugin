package org.modelcatalogue.core

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.builder.RelationshipDefinition

class RelationshipService {

    static final long INDEX_STEP = 1000

    static transactional = true

    def modelCatalogueSecurityService
    def auditService

    /**
     * Executes the callback for each relationship found.
     *
     * This should be an cost effective alternative to fetching all relationships using CatalogueElement#incomingRelationships
     * or CatalogueElement#outgoingRelationships methods or the relationships shortcuts as it only fetches the relationships
     * in batches of 10.
     *
     * @param direction the direction of relationships to fetch
     * @param element the element to be used as source or destination of the relationships
     * @param type the type of relationship
     * @param callback closure executed for each relationship
     */
    void eachRelationshipPartitioned(RelationshipDirection direction, CatalogueElement element, RelationshipType type = null, @ClosureParams(value=FromString, options=['org.modelcatalogue.core.Relationship']) callback) {
        int offset = 0
        int max = 10


        List<Relationship> relationships = getRelationships(max: max, direction, element, type).items

        while(relationships) {
            for (Relationship rel in relationships) {
                callback rel
            }
            offset += max
            relationships = getRelationships(direction, element, type, offset: offset, max: max).items
        }
    }

    ListWithTotal<Relationship> getRelationships(Map params, RelationshipDirection direction, CatalogueElement element, RelationshipType type = null) {
        if (!params.sort) {
            params.sort = direction.sortProperty
        }
        Lists.fromCriteria(params, direction.composeWhere(element, type, element.instanceOf(User) ? ClassificationFilter.NO_FILTER : ClassificationFilter.from(modelCatalogueSecurityService.currentUser)))
    }

    /**
     * @deprecated use #link(Closure)
     */
    @Deprecated
    Relationship link(CatalogueElement theSource, CatalogueElement theDestination, RelationshipType type, Classification theClassification, boolean theArchived = false, boolean theIgnoreRules = false, boolean theResetIndices = false) {
        link RelationshipDefinition.create(theSource, theDestination, type)
                .withClassification(theClassification)
                .withArchived(theArchived)
                .withIgnoreRules(theIgnoreRules)
                .withResetIndices(theResetIndices)
                .definition
    }

    Relationship link(RelationshipDefinition relationshipDefinition) {
        if (relationshipDefinition.source?.id && relationshipDefinition.destination?.id && relationshipDefinition.relationshipType?.id) {
            Relationship relationshipInstance = relationshipDefinition.skipUniqueChecking ? null : findExistingRelationship(relationshipDefinition)

            if (relationshipInstance) {

                if (relationshipDefinition.metadata) {
                    relationshipInstance.ext = relationshipDefinition.metadata
                }

                if (!relationshipDefinition.resetIndices && relationshipInstance.archived == relationshipDefinition.archived) {
                    return relationshipInstance
                }
                if (relationshipDefinition.resetIndices) {
                    relationshipInstance.resetIndexes()
                }
                relationshipInstance.archived = relationshipDefinition.archived
                return relationshipInstance
            }
        }

        Relationship relationshipInstance = relationshipDefinition.createRelationship()

        if(!relationshipDefinition.ignoreRules) {
            if (relationshipDefinition.relationshipType.versionSpecific && !relationshipDefinition.relationshipType.system && !(relationshipDefinition.source.status in [ElementStatus.DRAFT, ElementStatus.UPDATED, ElementStatus.PENDING])) {
                relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedModel.add', [relationshipDefinition.source.status.toString()] as Object[], "Cannot add new elements to {0}. Please create a new version before adding any additional elements")
                return relationshipInstance
            }

        }

        relationshipInstance.validate()

        if (relationshipInstance.hasErrors()) {
            return relationshipInstance
        }

        def errorMessage = relationshipDefinition.relationshipType.validateSourceDestination(relationshipDefinition.source, relationshipDefinition.destination, relationshipDefinition.metadata)

        if (errorMessage instanceof String) {
            relationshipInstance.errors.rejectValue('relationshipType', errorMessage)
            return relationshipInstance
        }
        if (errorMessage instanceof List && errorMessage.size() > 1 && errorMessage.first() instanceof String) {
            if (errorMessage.size() == 2) {
                relationshipInstance.errors.rejectValue('relationshipType', errorMessage[0]?.toString(), errorMessage[1]?.toString())
            } else {
                relationshipInstance.errors.rejectValue('relationshipType', errorMessage[0]?.toString(), errorMessage[1] as Object[], errorMessage[2]?.toString())
            }
            return relationshipInstance
        }

        relationshipInstance.save(validate: false, flush: true)
        relationshipDefinition.source?.addToOutgoingRelationships(relationshipInstance)?.save(validate: false, flush: true)
        relationshipDefinition.destination?.addToIncomingRelationships(relationshipInstance)?.save(validate: false, flush: true)
        auditService.logNewRelation(relationshipInstance)
        
        if (relationshipDefinition.metadata) {
            relationshipInstance.ext = relationshipDefinition.metadata
        }

        log.debug "Created $relationshipDefinition"
        
        relationshipInstance
    }

    Relationship findExistingRelationship(RelationshipDefinition definition) {
        // language=HQL
        String query = """
            select rel from Relationship rel left join fetch rel.extensions
            where rel.source = :source
            and rel.destination = :destination
            and rel.relationshipType = :relationshipType
            and rel.classification = :classification
        """

        Map<String, Object> params = [source: definition.source, destination: definition.destination, relationshipType: definition.relationshipType, classification: definition.classification]

        if (!definition.classification) {
            query = """
                select rel from Relationship rel left join fetch rel.extensions
                where rel.source = :source
                and rel.destination = :destination
                and rel.relationshipType = :relationshipType
                and rel.classification is null
            """
            params.remove 'classification'
        }
        List<Relationship> relationships = Relationship.executeQuery(query, params)
        if (relationships)  {
            return relationships.first()
        }
        if (definition.relationshipType.bidirectional) {
            params.source = definition.destination
            params.destination = definition.source
            relationships = Relationship.executeQuery(query, params)
            return relationships ? relationships.first() : null
        }
        log.info "Relationship $definition checked for presence but not found. Finding relationship is slow, consider using 'skipUniqueChecking' flag for optimistic relationship linking."
        return null
    }


    /**
     * @deprecated use #link(Closure)
     */
    @Deprecated
    Relationship link(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, boolean archived = false, boolean ignoreRules = false) {
        link source, destination, relationshipType, null, archived, ignoreRules
    }

    Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, boolean ignoreRules = false) {
        unlink source, destination, relationshipType, null, ignoreRules
    }

    Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, Classification classification, boolean ignoreRules = false) {

        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = findExistingRelationship(RelationshipDefinition.create(source, destination, relationshipType).withClassification(classification).definition)

            if(!ignoreRules) {
                if (relationshipType.versionSpecific && !relationshipType.system && source.status != ElementStatus.DRAFT && source.status != ElementStatus.UPDATED && source.status != ElementStatus.DEPRECATED) {
                    relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedDataElement.remove', [source.status.toString()] as Object[], "Cannot add removed data elements from {0} models. Please create a new version of the MODEL before removing any additional elements or archive the element first if you want to delete it.")
                    return relationshipInstance
                }
            }

            if (relationshipInstance && source && destination) {
                auditService.logRelationRemoved(relationshipInstance)
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

        RelationshipType classification = RelationshipType.readByName('classification')

        String classifications = Relationship.executeQuery("""
            select r.source.name
            from Relationship as r
            where r.relationshipType.id = :classification
            and r.destination.id = :elementId
            order by r.source.name
        """, [classification: classification.id, elementId: element.id]).join(', ')

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

        RelationshipType classification = RelationshipType.readByName('classification')

        Relationship.executeQuery("""
            select r.source.name, r.source.id, r.source.status
            from Relationship as r
            where r.relationshipType.id = :classification
            and r.destination.id = :elementId
            order by r.source.name
        """, [classification: classification.id, elementId: element.id]).collect {
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

        RelationshipType classification = RelationshipType.readByName('classification')

        Classification.executeQuery """
            select c
            from Classification as c
            join c.outgoingRelationships as rel
            where rel.relationshipType.id = :classification
            and rel.destination.id = :elementId
            order by c.name
        """, [classification: classification.id, elementId: element.id], [cache: true]
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
            return relationship.save(deepValidate: false, flush: true)
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
            return relationship.save(deepValidate: false, flush: true)
        }

        if (nextIndex - direction.getIndex(other) > 1) {
            direction.setIndex(relationship, direction.getIndex(other) + Math.round((nextIndex.doubleValue() - direction.getIndex(other)) / 2))
            return relationship.save(deepValidate: false, flush: true)
        }

        moveAfterWithRearrange(direction, owner, relationship, other)
    }

    private static Relationship moveAfterWithRearrange(RelationshipDirection direction, CatalogueElement owner, Relationship relationship, Relationship other) {
        List<Relationship> relationships = direction.composeWhere(owner, relationship.relationshipType, ClassificationFilter.NO_FILTER).list([sort: direction.sortProperty])
        int correction = 0
        relationships.eachWithIndex { Relationship entry, i ->
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
