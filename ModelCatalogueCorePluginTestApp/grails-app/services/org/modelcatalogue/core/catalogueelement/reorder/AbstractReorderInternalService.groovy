package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.events.CatalogueElementNotFoundEvent
import org.modelcatalogue.core.events.CatalogueElementStatusNotInDraftEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.RelationshipMovedEvent
import org.modelcatalogue.core.events.RelationshipNotFoundEvent
import org.modelcatalogue.core.events.RelationshipTypeNotFoundEvent
import org.modelcatalogue.core.events.UnauthorizedEvent
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.util.RelationshipDirection

@CompileStatic
abstract class AbstractReorderInternalService {

    DataModelGormService dataModelGormService

    RelationshipService relationshipService

    RelationshipGormService relationshipGormService

    abstract CatalogueElement findById(Long id)

    MetadataResponseEvent reorderInternal(RelationshipDirection direction, Long catalogueElementId, String type, Long movedId, Long currentId) {
        // begin sanity checks
        //check the user has the minimum role needed
        CatalogueElement owner = findById(catalogueElementId)
        if (!owner) {
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
        if (!rel) {
            return new RelationshipNotFoundEvent()
        }

        Relationship current = currentId ? relationshipGormService.findById(currentId) : null

        if (!current && currentId) {
            return new RelationshipNotFoundEvent()
        }

        if (current && current.relationshipType.versionSpecific && current.source.status != ElementStatus.DRAFT) {
            return new CatalogueElementStatusNotInDraftEvent()
        }

        new RelationshipMovedEvent(relationship: relationshipService.moveAfter(direction, owner, rel, current))
    }
}
