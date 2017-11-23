package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.security.Role
import org.springframework.context.MessageSource
import org.springframework.transaction.interceptor.TransactionAspectSupport

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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
            warnErrors(role, messageSource)
        }
        role
    }
}
