package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
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

    @Transactional
    void delete(RegistrationCode registrationCode) {
        registrationCode.delete()
    }

    @Transactional(readOnly = true)
    RegistrationCode findByToken(String token) {
        findQueryByToken(token).get()
    }

    protected DetachedCriteria<RegistrationCode> findQueryByToken(String tokenParam) {
        RegistrationCode.where { token == tokenParam }
    }

    DetachedCriteria<RegistrationCode> queryByIds(List<Long> ids) {
        RegistrationCode.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<RegistrationCode> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<RegistrationCode>
        }
        queryByIds(ids).list()
    }
}
