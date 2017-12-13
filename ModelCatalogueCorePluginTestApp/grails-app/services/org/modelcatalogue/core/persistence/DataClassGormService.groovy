package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

class DataClassGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    DataClass findById(long id) {
        DataClass.get(id)
    }

    @Transactional
    DataClass saveWithNameAndDescription(String name, String description) {
        save(new DataClass(name: name, description: description))
    }

    @Transactional
    DataClass save(DataClass dataClassInstance) {
        if ( !dataClassInstance.save() ) {
            warnErrors(dataClassInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        dataClassInstance
    }

    @Transactional
    DataClass saveWithNameAndDescriptionAndStatus(String name, String description, ElementStatus status) {
        save(new DataClass(name: name, description: description, status: status))
    }
}
