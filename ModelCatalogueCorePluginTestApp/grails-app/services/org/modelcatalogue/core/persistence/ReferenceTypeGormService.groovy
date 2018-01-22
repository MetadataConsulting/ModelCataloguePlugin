package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

class ReferenceTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    ReferenceType saveWithStatusAndNameAndDataClass(ElementStatus status, String name, DataClass dataClass) {
        ReferenceType referenceTypeInstance = new ReferenceType(status: status, name: name, dataClass: dataClass)
        save(referenceTypeInstance)
    }

    @Transactional
    ReferenceType save(ReferenceType referenceTypeInstance) {
        if ( !referenceTypeInstance.save() ) {
            warnErrors(referenceTypeInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        referenceTypeInstance
    }

    @Transactional(readOnly = true)
    ReferenceType findById(Long id) {
        ReferenceType.get(id)
    }
}
