package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.RelationshipTypeName
import org.modelcatalogue.core.WarnGormErrors
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
class RelationshipTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    RelationshipType findById(long id) {
        RelationshipType.get(id)
    }

    @Transactional(readOnly = true)
    RelationshipType findByRelationshipTypeName(RelationshipTypeName relationshipTypeName) {
        final String nameParam = relationshipTypeName.name
        findByName(nameParam)
    }

    @Transactional(readOnly = true)
    RelationshipType findByName(String nameParam) {
        RelationshipType.where {
            name == nameParam
        }.get()
    }

    @Transactional(readOnly = true)
    List<RelationshipType> findRelationshipTypes() {
        RelationshipType.where { }.list()
    }

    @Transactional
    RelationshipType save(Map m) {
        RelationshipType relationshipTypeInstance = new RelationshipType(m)
        save(relationshipTypeInstance)
    }

    @Transactional
    RelationshipType save(RelationshipType relationshipTypeInstance) {
        if ( !relationshipTypeInstance.save() ) {
            warnErrors(relationshipTypeInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        relationshipTypeInstance
    }

    @Transactional
    RelationshipType saveWithNameAndSourceDestinationAndDestinationToSourceAndSourceClassAndDestinationClass(String name,
                                                                                                             String sourceToDestination,
                                                                                                             String destinationToSource,
                                                                                                             Class sourceClass,
                                                                                                             Class destinationClass) {
        RelationshipType relationshipTypeInstance = new RelationshipType(name: name,
                sourceToDestination: sourceToDestination,
                destinationToSource: destinationToSource,
                sourceClass: sourceClass,
                destinationClass: destinationClass)
        save(relationshipTypeInstance)
    }

    @Transactional
    RelationshipType saveWithNameAndSourceDestinationAndDestinationToSourceAndSourceClassAndDestinationClassAndRule(String name,
                                                                                                             String sourceToDestination,
                                                                                                             String destinationToSource,
                                                                                                             Class sourceClass,
                                                                                                             Class destinationClass,
                                                                                                             String rule) {
        RelationshipType relationshipTypeInstance = new RelationshipType(name: name,
                sourceToDestination: sourceToDestination,
                destinationToSource: destinationToSource,
                sourceClass: sourceClass,
                destinationClass: destinationClass,
                rule: rule
        )
        save(relationshipTypeInstance)
    }

    DetachedCriteria<RelationshipType> queryByIds(List<Long> ids) {
        RelationshipType.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<RelationshipType> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<RelationshipType>
        }
        queryByIds(ids).list()
    }

    @Override
    Logger getLog() {
        return log
    }
}
