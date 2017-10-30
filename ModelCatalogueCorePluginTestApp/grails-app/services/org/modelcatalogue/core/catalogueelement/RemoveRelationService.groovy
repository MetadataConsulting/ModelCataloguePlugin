package org.modelcatalogue.core.catalogueelement

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.RelationshipNotFoundEvent
import org.modelcatalogue.core.events.RelationshipRemovedEvent
import org.modelcatalogue.core.events.RelationshipTypeNotFoundEvent
import org.modelcatalogue.core.events.RelationshipWithErrorsEvent
import org.modelcatalogue.core.persistence.DataModelGormService

class RemoveRelationService {

    DataModelGormService dataModelGormService

    RelationshipService relationshipService

    MetadataResponseEvent removeRelation(String relationshipTypeName,
                                         Long dataModelId,
                                         boolean outgoing,
                                         CatalogueElement source,
                                         CatalogueElement destination) {
        RelationshipType relationshipType = RelationshipType.readByName(relationshipTypeName)
        if (!relationshipType) {
            return new RelationshipTypeNotFoundEvent()
        }

        DataModel dataModel = dataModelId ? dataModelGormService.findById(dataModelId) : null

        Relationship old
        if ( outgoing ) {
            old = relationshipService.unlink(source, destination, relationshipType, dataModel)
        } else {
            old = relationshipService.unlink(destination, source, relationshipType, dataModel)
        }
        if (!old) {
            return new RelationshipNotFoundEvent()
        }

        if (old.hasErrors()) {
            return new RelationshipWithErrorsEvent()
        }

        new RelationshipRemovedEvent()
    }
}
