package org.modelcatalogue.core.security

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class UserRoleGormService {

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
            log.warn('unable to find user with username' + username)
            return
        }
        Role role = roleGormService.findByAuthority(authority)
        if ( role == null ) {
            log.warn('unable to find role with authority' + authority)
            return
        }
        userRole = new UserRole(role: role, user: user)
        if ( !userRole.save() ) {
            log.error('error while saving user role')
        }
        userRole
    }

    protected DetachedCriteria<UserRole> findQueryByUsernameAndAuthority(String username, String authority) {
        UserRole.where { user.username == 'username' && role.authority == authority }
    }
}
