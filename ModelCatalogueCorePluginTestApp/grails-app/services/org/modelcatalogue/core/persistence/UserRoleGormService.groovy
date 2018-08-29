package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
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
        saveUserRoleByUserAndRole(user, role)
    }

    @Transactional
    UserRole saveUserRoleByUserAndRole(User user, Role role) {
        List<UserRole> userRoleList = findQueryByUserAndRole(user, role).list()
        if ( userRoleList ) {
            return userRoleList.first()
        }
        UserRole userRole = new UserRole(role: role, user: user)
        if ( !userRole.save() ) {
            warnErrors(userRole, messageSource)
            transactionStatus.setRollbackOnly()
        }
        userRole
    }

    protected DetachedCriteria<UserRole> findQueryByUserAndRole(User userParam, Role roleParam) {
        UserRole.where { user == userParam && role == roleParam }
    }


    protected DetachedCriteria<UserRole> findQueryByUsernameAndAuthority(String username, String authority) {
        UserRole.where { user.username == 'username' && role.authority == authority }
    }

    List<UserRole> findAllByAuthority(String authority) {
        findQueryByAuthority(authority).list()
    }

    protected DetachedCriteria<UserRole> findQueryByAuthority(String authority) {
        UserRole.where { role.authority == authority }
    }

    @Transactional(readOnly = true)
    Set<Role> findRolesByUser(User user) {
        findQueryByUser(user).collect { it.role } as Set<Role>
    }

    protected
    DetachedCriteria<UserRole> findQueryByUser(User userParam ) {
        UserRole.where { user == userParam }
    }

    @Transactional(readOnly = true)
    List<UserRole> findAllByRole(Role role) {
        queryByRole(role).list()
    }

    DetachedCriteria<UserRole> queryByRole(Role roleParam) {
        UserRole.where { role == roleParam }

    }

    @Transactional(readOnly = true)
    Boolean hasRole(Long userId, String authority) {
        UserRole.where { user == User.load(userId) && role.authority == authority }.count() as Boolean
    }

    @Override
    Logger getLog() {
        return log
    }
}
