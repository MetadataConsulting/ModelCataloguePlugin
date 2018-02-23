package org.modelcatalogue.core

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.persistence.RoleGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.UserRole
import org.modelcatalogue.core.security.UserService
import org.springframework.context.MessageSource

import javax.annotation.PostConstruct

@CompileStatic
class MaxActiveUsersService {

    static transactional = false

    RoleGormService roleGormService
    UserGormService userGormService
    UserRoleGormService userRoleGormService
    GrailsApplication grailsApplication
    Integer maxUsers

    @CompileDynamic
    @PostConstruct
    private void init() {
        maxUsers = grailsApplication.config.mc.max.active.users ?: null
    }

    boolean maxActiveUsers() {
        if ( !maxUsers ) {
            return false
        }

        Number numOfUsers = numberOfEnabledUsers()

        numOfUsers >= maxUsers
    }

    Number numberOfEnabledUsers() {
        Role supervisorRole = roleGormService.findByAuthority(MetadataRoles.ROLE_SUPERVISOR)
        if ( supervisorRole ) {
            return numberOfUsersExceptUsersWithRole(supervisorRole)
        }

        userGormService.countByEnabled(true)
    }

    Number numberOfUsersExceptUsersWithRole(Role role) {
        List<UserRole> supervisorsUserRole = userRoleGormService.findAllByRole(role)

        if (supervisorsUserRole) {
            List<String> usernameList = supervisorsUserRole*.user*.username
            return userGormService.countByEnabledAndUsernameNotInList(true, usernameList)
        }
        userGormService.countByEnabled(true)
    }

}
