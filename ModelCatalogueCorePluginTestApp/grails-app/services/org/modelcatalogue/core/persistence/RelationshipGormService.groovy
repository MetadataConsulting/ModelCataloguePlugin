package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

class RelationshipGormService {

    @Transactional
    Relationship findById(long id) {
        Relationship.get(id)
    }

    @Transactional(readOnly = true)
    List<Relationship> findAllByRelationshipTypeAndSource(RelationshipType relationshipType, CatalogueElement source) {
        findQueryByRelationshipTypeAndSource(relationshipType, source).list() as List<Relationship>
    }

    protected DetachedCriteria<Relationship> findQueryByRelationshipTypeAndSource(RelationshipType relationshipTypeParam, CatalogueElement sourceParam) {
        Relationship.where { relationshipType == relationshipTypeParam && source == sourceParam }
    }

    @Transactional(readOnly = true)
    Relationship findBySourceAndDestinationAndRelationshipType(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        findQueryBySourceAndDestinationAndRelationshipType(source, destination, relationshipType).get()
    }

    protected DetachedCriteria<Relationship> findQueryBySourceAndDestinationAndRelationshipType(CatalogueElement sourceParam, CatalogueElement destinationParam, RelationshipType relationshipTypeParam) {
        Relationship.where { source == sourceParam && destination == destinationParam && relationshipType == relationshipTypeParam  }
    }
}

