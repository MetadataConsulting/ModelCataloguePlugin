package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

class EnumeratedTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    EnumeratedType findById(Long id) {
        EnumeratedType.get(id)
    }

    @Transactional
    EnumeratedType saveWithStatusAndNameAndEnumerations(ElementStatus status, String name, Map enumerations) {
        save(new EnumeratedType(status: status, name: name, enumerations: enumerations))
    }

    @Transactional
    EnumeratedType save(EnumeratedType enumeratedTypeInstance) {
        if (!enumeratedTypeInstance.save()) {
            warnErrors(enumeratedTypeInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        enumeratedTypeInstance
    }
}

