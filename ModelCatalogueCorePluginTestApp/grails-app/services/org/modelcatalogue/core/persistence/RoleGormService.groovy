package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.security.Role
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
class RoleGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    Role findByAuthority(String authority) {
        findQueryByAuthority(authority).get()
    }

    protected DetachedCriteria<Role> findQueryByAuthority(String authorityParam) {
        Role.where { authority == authorityParam }
    }

    @Transactional
    Role saveByAuthority(String authority) {
        Role role = new Role(authority: authority)
        if ( !role.save() ) {
            transactionStatus.setRollbackOnly()
            warnErrors(role, messageSource)
        }
        role
    }

    @Override
    Logger getLog() {
        return log
    }
}
