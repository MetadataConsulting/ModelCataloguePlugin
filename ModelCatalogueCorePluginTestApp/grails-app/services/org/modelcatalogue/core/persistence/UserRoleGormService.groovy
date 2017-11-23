package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole
import org.springframework.context.MessageSource
import org.springframework.transaction.interceptor.TransactionAspectSupport

@CompileStatic
class UserRoleGormService implements WarnGormErrors {

    MessageSource messageSource

    RoleGormService roleGormService

    UserGormService userGormService

    @Transactional
    UserRole saveUserRoleByUsernameAndAuthority(String username, String authority) {
        UserRole userRole = findQueryByUsernameAndAuthority(username, authority).get()
        if ( userRole != null ) {
            return userRole
        }
        User user = userGormService.findByUsername(username)
        if ( user == null ) {
            log.warn("unable to find user with username ${username}".toString())
            return
        }
        Role role = roleGormService.findByAuthority(authority)
        if ( role == null ) {
            log.warn("unable to find role with authority ${authority}".toString())
            return
        }
        userRole = new UserRole(role: role, user: user)
        if ( !userRole.save() ) {
            warnErrors(userRole, messageSource)
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
        }
        userRole
    }


    protected DetachedCriteria<UserRole> findQueryByUsernameAndAuthority(String username, String authority) {
        UserRole.where { user.username == 'username' && role.authority == authority }
    }

    protected List<UserRole> findAllByAuthority(String authority) {
        findQueryByAuthority(authority).list()
    }

    protected DetachedCriteria<UserRole> findQueryByAuthority(String authority) {
        UserRole.where { role.authority == authority }
    }
}
