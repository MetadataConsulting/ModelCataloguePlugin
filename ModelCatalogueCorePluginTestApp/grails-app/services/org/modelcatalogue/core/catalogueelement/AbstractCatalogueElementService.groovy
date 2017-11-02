package org.modelcatalogue.core.catalogueelement

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipDefinition
import org.modelcatalogue.core.RelationshipDefinitionBuilder
import org.modelcatalogue.core.RelationshipService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.events.CatalogueElementArchivedEvent
import org.modelcatalogue.core.events.CatalogueElementNotFoundEvent
import org.modelcatalogue.core.events.CatalogueElementRestoredEvent
import org.modelcatalogue.core.events.CatalogueElementStatusNotDeprecatedEvent
import org.modelcatalogue.core.events.CatalogueElementStatusNotInDraftEvent
import org.modelcatalogue.core.events.CatalogueElementStatusNotFinalizedEvent
import org.modelcatalogue.core.events.CatalogueElementWithErrorsEvent
import org.modelcatalogue.core.events.DataModelNotFoundEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.RelationAddedEvent
import org.modelcatalogue.core.events.RelationshipMovedEvent
import org.modelcatalogue.core.events.RelationshipNotFoundEvent
import org.modelcatalogue.core.events.RelationshipTypeNotFoundEvent
import org.modelcatalogue.core.events.RelationshipWithErrorsEvent
import org.modelcatalogue.core.events.RelationshipsEvent
import org.modelcatalogue.core.events.UnauthorizedEvent
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.util.DestinationClass
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.SearchParams
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships

abstract class AbstractCatalogueElementService implements ManageCatalogueElementService {

    DataModelGormService dataModelGormService

    ElementService elementService

    RelationshipService relationshipService

    RelationshipGormService relationshipGormService

    CatalogueElementGormService catalogueElementGormService

    def modelCatalogueSearchService

    abstract CatalogueElement findById(Long id)

    abstract protected String resourceName()

    @Override
    MetadataResponseEvent searchWithinRelationships(Long catalogueElementId,
                                                    String type,
                                                    RelationshipDirection direction,
                                                    SearchParams searchParams) {

        CatalogueElement element = findById(catalogueElementId)
        if ( element == null ) {
            return new CatalogueElementNotFoundEvent()
        }

        RelationshipType relationshipType = RelationshipType.readByName(type)
        if (!element) {
            return new RelationshipTypeNotFoundEvent()
        }

        Map params = searchParams.paramArgs.toMap()
        ListWithTotalAndType<Relationship> results = modelCatalogueSearchService.search(element, relationshipType, direction, searchParams)

        String searchEncoded = URLEncoder.encode(searchParams.search, 'UTF-8')
        String base = "/${resourceName()}/${catalogueElementId}/${direction.actionName}" + (type ? "/${type}" : "") + "/search?search=${searchEncoded ?: ''}"
        Relationships relationships = new Relationships(owner: element,
                direction: direction,
                type: relationshipType,
                list: Lists.wrap(params, base, results))
        new RelationshipsEvent(relationships: relationships)
    }

    @Override
    MetadataResponseEvent archive(Long catalogueElementId) {

        CatalogueElement instance = findById(catalogueElementId)

        if (instance == null) {
            return new CatalogueElementNotFoundEvent()
        }

        if ( !dataModelGormService.isAdminOrHasAdministratorPermission(instance) ) {
            return new UnauthorizedEvent()
        }

        if ( !(instance.status == ElementStatus.FINALIZED) ) {
            return new CatalogueElementStatusNotFinalizedEvent()
        }

        // do not archive relationships as we need to transfer the deprecated elements to the new versions
        instance = elementService.archive(instance, false)

        if (instance.hasErrors()) {
            return new CatalogueElementWithErrorsEvent(catalogueElement: instance)
        }

        new CatalogueElementArchivedEvent(catalogueElement: instance)
    }

    @Override
    MetadataResponseEvent restore(Long catalogueElementId) {

        CatalogueElement instance = findById(catalogueElementId)

        if ( instance == null ) {
            return new CatalogueElementNotFoundEvent()
        }

        if ( !dataModelGormService.isAdminOrHasAdministratorPermission(instance) ) {
            return new UnauthorizedEvent()
        }

        if ( !(instance.status == ElementStatus.DEPRECATED) ) {
            return new CatalogueElementStatusNotDeprecatedEvent()
        }

        instance = elementService.restore(instance)

        if (instance.hasErrors()) {
            return new CatalogueElementWithErrorsEvent(catalogueElement: instance)
        }

        new CatalogueElementRestoredEvent(catalogueElement: instance)
    }

    @Override
    MetadataResponseEvent reorderInternal(RelationshipDirection direction, Long catalogueElementId, String type, Long movedId, Long currentId) {
        // begin sanity checks
        //check the user has the minimum role needed
        CatalogueElement owner = findById(catalogueElementId)

        if ( owner == null ) {
            return new CatalogueElementNotFoundEvent()
        }

        if ( !dataModelGormService.isAdminOrHasAdministratorPermission(owner) ) {
            return new UnauthorizedEvent()
        }

        RelationshipType relationshipType = RelationshipType.readByName(type)
        if ( !relationshipType ) {
            return new RelationshipTypeNotFoundEvent()
        }

        Relationship rel = relationshipGormService.findById(movedId)
        if ( rel == null ) {
            return new RelationshipNotFoundEvent()
        }

        Relationship current = currentId ? relationshipGormService.findById(currentId) : null

        if ( (current == null) && currentId ) {
            return new RelationshipNotFoundEvent()
        }

        if (current && current.relationshipType.versionSpecific && current.source.status != ElementStatus.DRAFT) {
            return new CatalogueElementStatusNotInDraftEvent()
        }

        new RelationshipMovedEvent(relationship: relationshipService.moveAfter(direction, owner, rel, current))
    }

    /**
     * @param otherSide - request JSON as an JSONObject or an JSONArray
     */
    @Override
    @Transactional
    MetadataResponseEvent addRelation(Long catalogueElementId,
                                      String type,
                                      Boolean outgoing,
                                      Object objectToBind,
                                      DestinationClass otherSide) throws ClassNotFoundException {
        CatalogueElement source = findById(catalogueElementId)
        if ( source == null ) {
            return new CatalogueElementNotFoundEvent()
        }
        RelationshipType relationshipType = RelationshipType.readByName(type)
        if ( relationshipType == null ) {
            return new RelationshipNotFoundEvent()
        }

        Object newDataModel = objectToBind['__dataModel'] ?: objectToBind['__classification']
        Long dataModelId = newDataModel instanceof Map ? newDataModel['id'] as Long : null

        DataModel dataModel
        if ( dataModelId ) {
            dataModel = dataModelGormService.findById(dataModelId)
            if ( dataModel == null ) {
                return new DataModelNotFoundEvent()
            }
        }

        Object oldDataModel = objectToBind['__oldDataModel'] ?: objectToBind['__oldClassification']
        Long oldDataModelId = oldDataModel instanceof Map ? oldDataModel['id'] as Long : null

        DataModel oldDataModelInstance
        if ( oldDataModelId ) {
            oldDataModelInstance = dataModelGormService.findById(oldDataModelId)
            if ( oldDataModelInstance == null ) {
                return new DataModelNotFoundEvent()
            }
        }

        CatalogueElement destination = findDestinationByOtherSide(otherSide)
        if ( destination == null ) {
            return new CatalogueElementNotFoundEvent()
        }

        if (oldDataModelInstance != dataModel) {
            if (outgoing) {
                relationshipService.unlink(source, destination, relationshipType, oldDataModelInstance)
            } else {
                relationshipService.unlink(destination, source, relationshipType, oldDataModelInstance)
            }
        }

        RelationshipDefinitionBuilder definition = outgoing ? RelationshipDefinition.create(source, destination, relationshipType) : RelationshipDefinition.create(destination, source, relationshipType)

        definition.withDataModel(dataModel).withMetadata(metadataFromObjectToBind(objectToBind))

        if ( isSupervisor() ) {
            definition.withIgnoreRules(true)
        }
        Relationship rel = relationshipService.link(definition.definition)

        if (rel.hasErrors()) {
            return new RelationshipWithErrorsEvent(rel: rel)
        }

        RelationshipDirection direction = outgoing ? RelationshipDirection.OUTGOING : RelationshipDirection.INCOMING

        rel.save(flush: true, deepValidate: false, validate: false)

        new RelationAddedEvent(rel: rel, source: source, direction: direction)
    }

    private boolean isSupervisor() {
        SpringSecurityUtils.ifAnyGranted(MetadataRolesUtils.getRolesFromAuthority('SUPERVISOR').join(','))
    }

    @CompileDynamic
    private Map<String, String> metadataFromObjectToBind(Object objectToBind) {
        OrderedMap.fromJsonMap(objectToBind.metadata ?: [:])
    }

    private CatalogueElement findDestinationByOtherSide(DestinationClass otherSide) {
        Class otherSideType = Class.forName otherSide.className

        catalogueElementGormService.findCatalogueElementByClassAndId(otherSideType, otherSide.id)
    }
}
