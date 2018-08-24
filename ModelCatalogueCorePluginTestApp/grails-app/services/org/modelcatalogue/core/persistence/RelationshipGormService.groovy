package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.WarnGormErrors
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
class RelationshipGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    Relationship findById(long id) {
        Relationship.get(id)
    }

    @Transactional
    Relationship save(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        save(new Relationship(source: source, destination: destination, relationshipType: relationshipType))
    }

    @Transactional
    Relationship save(Relationship relationship) {
        if ( !relationship.save() ) {
            warnErrors(relationship, messageSource)
            transactionStatus.setRollbackOnly()
        }
        relationship
    }

    @Transactional(readOnly = true)
    List<Relationship> findAllByDestinationIdAndRelationshipTypeSourceToDestination(Long destinationId, String relationshipTypeSourceToDestination) {
        queryByDestinationIdAndRelationshipTypeSourceToDestination(destinationId, relationshipTypeSourceToDestination).list()
    }

    DetachedCriteria<Relationship> queryByDestinationIdAndRelationshipTypeSourceToDestination(Long destinationId, String relationshipTypeSourceToDestination) {
        Relationship.where {
            destination.id == destinationId && relationshipType.sourceToDestination == relationshipTypeSourceToDestination
        }
    }

    @Transactional(readOnly = true)
    Number countByRelationshipTypeAndSourceAndDestination(RelationshipType relationshipType, CatalogueElement source, CatalogueElement destination) {
        findQueryByRelationshipTypeAndSourceAndDestination(relationshipType, source, destination).count()
    }

    @Transactional(readOnly = true)
    List<Relationship> findAllByRelationshipTypeAndSourceAndDestination(RelationshipType relationshipType, CatalogueElement source, CatalogueElement destination) {
        findQueryByRelationshipTypeAndSourceAndDestination(relationshipType, source, destination).list()
    }

    protected DetachedCriteria<Relationship> findQueryByRelationshipTypeAndSourceAndDestination(RelationshipType relationshipTypeParam,
                                                                                  CatalogueElement sourceParam,
                                                                                  CatalogueElement destinationParam) {
        Relationship.where { relationshipType == relationshipTypeParam && source == sourceParam && destination == destinationParam }
    }

    @Transactional(readOnly = true)
    List<Relationship> findAllByRelationshipTypeAndSource(RelationshipType relationshipType, CatalogueElement source) {
        findQueryByRelationshipTypeAndSource(relationshipType, source).list() as List<Relationship>
    }

    DetachedCriteria<Relationship> findQueryByRelationshipTypeAndSource(RelationshipType relationshipTypeParam, CatalogueElement sourceParam) {
        Relationship.where { relationshipType == relationshipTypeParam && source == sourceParam }
    }

    DetachedCriteria<Relationship> queryByIds(List<Long> ids) {
        Relationship.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<Relationship> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<Relationship>
        }
        queryByIds(ids).list()
    }

    @Override
    Logger getLog() {
        return log
    }
}

