package org.modelcatalogue.core

import com.google.common.collect.ImmutableMap
import grails.gorm.DetachedCriteria
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.Inheritance
import org.modelcatalogue.core.util.RelationshipsCounts
import org.modelcatalogue.core.util.lists.ListWithTotal
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.RelationshipDirection


import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder

class RelationshipService {

    private static final Cache<Long, Set<Long>> FAVORITE_CACHE = CacheBuilder.newBuilder().initialCapacity(20).build()
    private static final Cache<Long, RelationshipsCounts> RELATIONSHIPS_COUNT_CACHE = CacheBuilder.newBuilder().initialCapacity(1000).build()

    static final long INDEX_STEP = 1000

    /**
     * It's not desired if it triggers any transaction itself. It should just support the transactions already running.
     *
     * Especially method #getDataModels() can be called in various places where forcing transaction may cause an
     * exception.
     */
    static transactional = false

    def modelCatalogueSecurityService
    def auditService
    def dataModelService

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
        Lists.fromCriteria(params, direction.composeWhere(element, type, element.instanceOf(User) ? DataModelFilter.NO_FILTER : DataModelFilter.from(modelCatalogueSecurityService.currentUser)))
    }

    /**
     * @deprecated use #link(Closure)
     */
    @Deprecated
    Relationship link(CatalogueElement theSource, CatalogueElement theDestination, RelationshipType type, DataModel theDataModel, boolean theArchived = false, boolean theIgnoreRules = false, boolean theResetIndices = false) {
        link RelationshipDefinition.create(theSource, theDestination, type)
                .withDataModel(theDataModel)
                .withArchived(theArchived)
                .withIgnoreRules(theIgnoreRules)
                .withResetIndices(theResetIndices)
                .definition
    }

    Relationship link(RelationshipDefinition relationshipDefinition) {
        if (relationshipDefinition.source?.readyForQueries && relationshipDefinition.destination?.readyForQueries && relationshipDefinition.relationshipType?.getId()) {
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


        if (relationshipDefinition.relationshipType == RelationshipType.baseType) {
            // copy relationships when new based on relationship is created
            for (Relationship relationship in new LinkedHashSet<Relationship>(relationshipDefinition.source.outgoingRelationships)) {
                if (relationship.relationshipType.versionSpecific) {
                    RelationshipDefinition newDefinition = RelationshipDefinition.from(relationship)
                    newDefinition.source = relationshipDefinition.destination
                    newDefinition.inherited = true
                    Relationship newRelationship = link newDefinition
                    if (newRelationship.hasErrors()) {
                        relationshipInstance.errors.reject('unable.to.copy.from.parent', FriendlyErrors.printErrors("Unable to copy relationship $newDefinition from ${relationshipDefinition.source} to child ${relationshipDefinition.destination}", newRelationship.errors))
                    }
                }
            }
            relationshipDefinition.destination.ext.putAll relationshipDefinition.source.ext.subMap(relationshipDefinition.source.ext.keySet() - relationshipDefinition.destination.ext.keySet())
            relationshipDefinition.source.addInheritedAssociations(relationshipDefinition.destination)
        } else if (relationshipDefinition.relationshipType.versionSpecific) {
            // propagate relationship to the children
            Inheritance.withChildren(relationshipInstance.source) {
                RelationshipDefinition newDefinition = relationshipDefinition.clone()
                newDefinition.source = it
                newDefinition.inherited = true
                Relationship newRelationship = link newDefinition
                if (newRelationship.hasErrors()) {
                    relationshipInstance.errors.reject('unable.to.copy.from.parent', FriendlyErrors.printErrors("Unable to propagate relationship $newDefinition from ${relationshipDefinition.source} to child ${newDefinition.source}", newRelationship.errors))
                }
            }
        }

        if (relationshipDefinition.relationshipType == RelationshipType.favouriteType) {
            FAVORITE_CACHE.invalidate(relationshipDefinition.source.getId())
        }

        RELATIONSHIPS_COUNT_CACHE.invalidate(relationshipDefinition.source.getId())
        RELATIONSHIPS_COUNT_CACHE.invalidate(relationshipDefinition.destination.getId())

        relationshipInstance
    }

    Relationship findExistingRelationship(RelationshipDefinition definition) {
        // language=HQL
        String query = """
            select rel from Relationship rel left join fetch rel.extensions
            where rel.source = :source
            and rel.destination = :destination
            and rel.relationshipType = :relationshipType
            and rel.dataModel = :dataModel
        """

        Map<String, Object> params = [source: definition.source, destination: definition.destination, relationshipType: definition.relationshipType, dataModel: definition.dataModel]

        if (!definition.dataModel) {
            query = """
                select rel from Relationship rel left join fetch rel.extensions
                where rel.source = :source
                and rel.destination = :destination
                and rel.relationshipType = :relationshipType
                and rel.dataModel is null
            """
            params.remove 'dataModel'
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

    Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType, DataModel dataModel, boolean ignoreRules = false, Map<String, String> expectedMetadata = null) {

        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = findExistingRelationship(RelationshipDefinition.create(source, destination, relationshipType).withDataModel(dataModel).definition)

            if (!relationshipInstance) {
                return null
            }

            if(!ignoreRules) {
                if (relationshipType.versionSpecific && !relationshipType.system && source.status != ElementStatus.DRAFT && source.status != ElementStatus.UPDATED && source.status != ElementStatus.DEPRECATED) {
                    relationshipInstance.errors.rejectValue('relationshipType', 'org.modelcatalogue.core.RelationshipType.sourceClass.finalizedDataElement.remove', [source.status.toString()] as Object[], "Cannot changed finalized elements.")
                    return relationshipInstance
                }

                if (relationshipInstance.inherited) {
                    relationshipInstance.errors.rejectValue('inherited', 'org.modelcatalogue.core.RelationshipType.cannot.change.inherited',  "Cannot changed inherited relationships.")
                    return relationshipInstance
                }

            }

            if (expectedMetadata != null && expectedMetadata != relationshipInstance.ext) {
                return null
            }

            auditService.logRelationRemoved(relationshipInstance)

            destination.refresh()
            source.refresh()

            if (relationshipType == RelationshipType.baseType) {
                for (Relationship relationship in new LinkedHashSet<Relationship>(source.outgoingRelationships)) {
                    if (relationship.relationshipType.versionSpecific) {
                        unlink relationship.source, relationship.destination, relationship.relationshipType, relationship.dataModel, true, relationship.ext
                    }
                }
                List<String> forRemoval = []
                source.ext.each { String key, String value ->
                    String valueInChild = destination.ext[key]
                    if (valueInChild == value) {
                        forRemoval << key
                    }
                }
                forRemoval.each {
                    source.ext.remove(it)
                }
            } else if (relationshipType.versionSpecific) {
                Inheritance.withChildren(source) {
                    unlink(it, destination, relationshipType, dataModel, true, relationshipInstance.ext)
                }
            }

            destination.removeFromIncomingRelationships(relationshipInstance)
            source.removeFromOutgoingRelationships(relationshipInstance)
            relationshipInstance.source = null
            relationshipInstance.destination = null
            relationshipInstance.dataModel = null
            relationshipInstance.delete(flush: true)
            if (relationshipType == RelationshipType.baseType) {
                source.removeInheritedAssociations(destination)
            }

            if (relationshipType == RelationshipType.favouriteType) {
                FAVORITE_CACHE.invalidate(source.getId())
            }

            RELATIONSHIPS_COUNT_CACHE.invalidate(source.getId())
            RELATIONSHIPS_COUNT_CACHE.invalidate(destination.getId())

            return relationshipInstance
        }
        return null
    }


    /**
     * @deprecated overcomplicated
     */
    def getDataModelsInfo(CatalogueElement element) {
        if (!element) {
            return []
        }

        if (!element.getId()) {
            return []
        }

        if (!element.dataModel) {
            return []
        }

        [element.dataModel].collect {
            [name: it.name, id: it.getId(), status: "${it.status}", elementType: DataModel.name, link:  "/dataModel/${it.getId()}"]
        }
    }

    /**
     * @deprecated overcomplicated
     */
    def List<DataModel> getDataModels(CatalogueElement element) {
        if (!element || !element.dataModel) {
            return []
        }

        return [element.dataModel]
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
        List<Relationship> relationships = direction.composeWhere(owner, relationship.relationshipType, DataModelFilter.NO_FILTER).list([sort: direction.sortProperty])
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

    boolean isFavorite(CatalogueElement el) {
        if (modelCatalogueSecurityService.currentUser) {
            return el.getId() in FAVORITE_CACHE.get(modelCatalogueSecurityService.currentUser.getId()) {
                RelationshipType favorite = RelationshipType.favouriteType
                if (!favorite) {
                    return [] as Set<Long>
                }
                Relationship.where {
                    relationshipType == favorite && source == modelCatalogueSecurityService.currentUser
                }.list().collect { it.destination.id }.toSet()
            }
        }
        return false
    }

    static void clearCache() {
        FAVORITE_CACHE.invalidateAll()
        FAVORITE_CACHE.cleanUp()

        RELATIONSHIPS_COUNT_CACHE.invalidateAll()
        RELATIONSHIPS_COUNT_CACHE.cleanUp()
    }

    int countIncomingRelationshipsByType(CatalogueElement element, RelationshipType type) {
        getRelationshipsCounts(element).count(RelationshipDirection.INCOMING, type)
    }

    int countOutgoingRelationshipsByType(CatalogueElement element, RelationshipType type) {
        getRelationshipsCounts(element).count(RelationshipDirection.OUTGOING, type)
    }

    int countRelationshipsByType(CatalogueElement element, RelationshipType type) {
        getRelationshipsCounts(element).count(RelationshipDirection.BOTH, type)
    }

    int countRelationshipsByDirectionAndType(CatalogueElement element, RelationshipDirection direction, RelationshipType type) {
        getRelationshipsCounts(element).count(direction, type)
    }


    private RelationshipsCounts getRelationshipsCounts(CatalogueElement el) {
        RELATIONSHIPS_COUNT_CACHE.get(el.getId()) {
            prepareCounts(el)
        }
    }

    private RelationshipsCounts prepareCounts(CatalogueElement el) {
        // TODO: does it need to exclude archived relationships?
        if (!el.readyForQueries) {
            return RelationshipsCounts.EMPTY
        }

        DetachedCriteria<Relationship> incomingTypes = new DetachedCriteria<Relationship>(Relationship).build {
            projections {
                id()
                property('relationshipType.id')
            }
            eq 'destination.id', el.getId()
        }

        Map<Long, Integer> incomingCounts = dataModelService.classified(incomingTypes).list().countBy { row ->
            row[1] as Long
        }

        DetachedCriteria<Relationship> outgoingTypes = new DetachedCriteria<Relationship>(Relationship).build {
            projections {
                id()
                property('relationshipType.id')
            }
            eq 'source.id', el.getId()
        }

        Map<Long, Integer> outgoingCounts = dataModelService.classified(outgoingTypes).list().countBy { row ->
            row[1] as Long
        }

        return RelationshipsCounts.create(ImmutableMap.copyOf(incomingCounts), ImmutableMap.copyOf(outgoingCounts))
    }


}
