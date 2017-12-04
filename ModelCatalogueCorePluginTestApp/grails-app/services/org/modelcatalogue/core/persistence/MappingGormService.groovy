package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.WarnGormErrors
import org.springframework.context.MessageSource

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
}
