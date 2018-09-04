package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.WarnGormErrors
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
class MappingGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    Mapping saveWithSourceAndDestinationAndMapping(CatalogueElement source, CatalogueElement destination, String mapping) {
        save(new Mapping(source: source, destination: destination, mapping: mapping))
    }

    @Transactional
    Mapping save(Mapping mapping) {
        if (!mapping.save()) {
            warnErrors(mapping, messageSource)
            transactionStatus.setRollbackOnly()
        }
        mapping
    }

    @Transactional(readOnly = true)
    Mapping findById(Long id) {
        Mapping.get(id)
    }

    DetachedCriteria<Mapping> queryByIds(List<Long> ids) {
        Mapping.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<Mapping> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<Mapping>
        }
        queryByIds(ids).list()
    }

    @Override
    Logger getLog() {
        return log
    }
}
