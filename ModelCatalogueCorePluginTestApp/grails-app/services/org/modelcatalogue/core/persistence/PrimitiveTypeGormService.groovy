package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.WarnGormErrors
import org.springframework.context.MessageSource

class PrimitiveTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    PrimitiveType save(PrimitiveType primitiveType) {
        if ( !primitiveType.save() ) {
            warnErrors(primitiveType, messageSource)
            transactionStatus.setRollbackOnly()
        }
        primitiveType
    }
}
