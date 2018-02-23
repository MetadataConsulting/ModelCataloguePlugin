package org.modelcatalogue.core.security

import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.MaxActiveUsersService
import org.modelcatalogue.core.persistence.RoleGormService
import org.modelcatalogue.core.persistence.RoleHierarchyEntryGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService

@Slf4j
class InitSecurityService {

    RoleGormService roleGormService

    RoleHierarchyEntryGormService roleHierarchyEntryGormService

    UserGormService userGormService

    UserRoleGormService userRoleGormService

    SpringSecurityService springSecurityService

    MaxActiveUsersService maxActiveUsersService

    /**
     * @description if the MetadataRoles don't exist in the database it saves them
     */
    void initRoles() {
        for ( String authority : MetadataRolesUtils.findAll() ) {
            if ( !roleGormService.findByAuthority(authority) ) {
                roleGormService.saveByAuthority(authority)
            }
        }
    }

    /**
     * @description if the role hierarchy doesn't exist in the database it saves it
     */
    void initRoleHierarchyEntry() {
        boolean reload = false
        for ( String entry : [
                    "${MetadataRoles.ROLE_SUPERVISOR} > ${MetadataRoles.ROLE_CURATOR}".toString(),
                    "${MetadataRoles.ROLE_CURATOR} > ${MetadataRoles.ROLE_USER}".toString(),
            ]) {
                if ( !roleHierarchyEntryGormService.findByEntry(entry) ) {
                    roleHierarchyEntryGormService.save(entry)
                    reload = true
                }
        }
        if ( reload ) {
            springSecurityService.reloadDBRoleHierarchy()
        }

    }

    void initUsers() {
        if ( maxActiveUsersService.maxActiveUsers() ) {
            log.info 'Limit of {} users has been reached', maxActiveUsersService.maxUsers
            return
        }
        for ( Map m : [
                [username: 'supervisor', password: System.getenv('MC_SUPERVISOR_PASSWORD') ?: 'supervisor', email: System.getenv(UserService.ENV_SUPERVISOR_EMAIL), apiKey: 'supervisorabcdef123456'],
                [username: 'user', password: 'user', apiKey: 'viewerabcdef123456'],
                [username: 'curator', password: 'curator', apiKey: 'curatorabcdef123456'],
        ] ) {
            if ( !userGormService.findByUsername(m.username as String) ) {
                User user = new User(name: m.username,
                        username: m.username,
                        enabled: true,
                        password: m.password,
                        email: m.email,
                        apiKey: m.apiKey)
                userGormService.save(user)
            }
        }
    }

    @CompileStatic
    void initUserRoles() {
        for ( Map<String, String> m : [
                [username: 'supervisor', authority: MetadataRoles.ROLE_SUPERVISOR],
                [username: 'curator', authority: MetadataRoles.ROLE_CURATOR],
                [username: 'user', authority: MetadataRoles.ROLE_USER],] as List< Map<String, String> >) {
            userRoleGormService.saveUserRoleByUsernameAndAuthority(m.username , m.authority)
        }
    }
}
