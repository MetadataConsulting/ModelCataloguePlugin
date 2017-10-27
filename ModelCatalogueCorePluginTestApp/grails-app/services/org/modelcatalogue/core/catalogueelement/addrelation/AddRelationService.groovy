package org.modelcatalogue.core.catalogueelement.addrelation

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipDefinition
import org.modelcatalogue.core.RelationshipDefinitionBuilder
import org.modelcatalogue.core.RelationshipService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.events.DataModelNotFoundEvent
import org.modelcatalogue.core.events.CatalogueElementNotFoundEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.RelationAddedEvent
import org.modelcatalogue.core.events.RelationshipNotFoundEvent
import org.modelcatalogue.core.events.RelationshipWithErrorsEvent
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.util.DestinationClass
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.RelationshipDirection

class AddRelationService {

    DataModelGormService dataModelGormService

    RelationshipService relationshipService

    CatalogueElementGormService catalogueElementGormService
    /**
     * @param otherSide - request JSON as an JSONObject or an JSONArray
     */
    @Transactional
    MetadataResponseEvent addRelation(Class resource,
                                      Long catalogueElementId,
                                      String type,
                                      Boolean outgoing,
                                      Object objectToBind,
                                      DestinationClass otherSide) throws ClassNotFoundException {
        CatalogueElement source = catalogueElementGormService.findCatalogueElementByClassAndId(resource, catalogueElementId)
        if ( !source ) {
            return new CatalogueElementNotFoundEvent()
        }
        RelationshipType relationshipType = RelationshipType.readByName(type)
        if (!relationshipType) {
            return new RelationshipNotFoundEvent()

        }

        Object newDataModel = objectToBind['__dataModel'] ?: objectToBind['__classification']
        Long dataModelId = newDataModel instanceof Map ? newDataModel['id'] as Long : null

        DataModel dataModel
        if ( dataModelId ) {
            dataModel = dataModelGormService.findById(dataModelId)
            if ( !dataModel ) {
                return new DataModelNotFoundEvent()
            }
        }

        Object oldDataModel = objectToBind['__oldDataModel'] ?: objectToBind['__oldClassification']
        Long oldDataModelId = oldDataModel instanceof Map ? oldDataModel['id'] as Long : null

        DataModel oldDataModelInstance
        if ( oldDataModelId ) {
            oldDataModelInstance = dataModelGormService.findById(oldDataModelId)
            if ( !oldDataModelInstance ) {
                return new DataModelNotFoundEvent()
            }
        }

        CatalogueElement destination = findDestinationByOtherSide(otherSide)
        if ( !destination ) {
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
