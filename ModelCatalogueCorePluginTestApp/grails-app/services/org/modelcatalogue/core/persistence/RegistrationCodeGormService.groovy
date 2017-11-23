package org.modelcatalogue.core.persistence

import grails.plugin.springsecurity.ui.RegistrationCode
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.springframework.context.MessageSource

class RegistrationCodeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    RegistrationCode saveWithUsername(String username) {
        RegistrationCode registrationCodeInstance = new RegistrationCode(username: username)
        if ( !registrationCodeInstance.save() ) {
            warnErrors(registrationCodeInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        registrationCodeInstance
    }
}
