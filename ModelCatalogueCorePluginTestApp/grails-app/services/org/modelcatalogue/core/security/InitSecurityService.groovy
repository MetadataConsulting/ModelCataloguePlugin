package org.modelcatalogue.core.security

import groovy.util.logging.Slf4j
import org.modelcatalogue.core.MaxActiveUsersService
import org.modelcatalogue.core.persistence.RoleGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService

@Slf4j
class InitSecurityService {

    RoleGormService roleGormService

    UserGormService userGormService

    UserRoleGormService userRoleGormService

    MaxActiveUsersService maxActiveUsersService

    /**
     * @description if the MetadataRoles don't exist in the database it saves them
     */
    void initRoles() {
        for ( String authority : [
                MetadataRoles.ROLE_USER,
                MetadataRoles.ROLE_ADMIN,
                MetadataRoles.ROLE_SUPERVISOR,
                MetadataRoles.ROLE_STACKTRACE,
                MetadataRoles.ROLE_CURATOR,
                MetadataRoles.ROLE_REGISTERED
        ] ) {
            if ( !roleGormService.findByAuthority(authority) ) {
                roleGormService.saveByAuthority(authority)
            }
        }
    }

    void initUsers() {
        if ( maxActiveUsersService.maxActiveUsers() ) {
            log.info 'Limit of {} users has been reached', maxActiveUsersService.maxUsers
            return
        }
        for ( Map m : [
                [username: 'supervisor', password: System.getenv('MC_SUPERVISOR_PASSWORD') ?: 'supervisor', email: System.getenv(UserService.ENV_SUPERVISOR_EMAIL), apiKey: 'supervisorabcdef123456'],
                [username: 'admin', password: 'admin', email: System.getenv('MC_ADMIN_EMAIL'), apiKey: 'adminabcdef123456'],
                [username: 'viewer', password: 'viewer', apiKey: 'viewerabcdef123456'],
                [username: 'curator', password: 'curator', apiKey: 'curatorabcdef123456'],
                [username: 'registered', password: 'registered', apiKey: 'registeredabcdef123456'],

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

    void initUserRoles() {
        for ( Map m : [
                [username: 'supervisor', authority: MetadataRoles.ROLE_USER],
                [username: 'supervisor', authority: MetadataRoles.ROLE_CURATOR],
                [username: 'supervisor', authority: MetadataRoles.ROLE_STACKTRACE],
                [username: 'supervisor', authority: MetadataRoles.ROLE_ADMIN],
                [username: 'supervisor', authority: MetadataRoles.ROLE_SUPERVISOR],
                [username: 'admin', authority: MetadataRoles.ROLE_USER],
                [username: 'admin', authority: MetadataRoles.ROLE_CURATOR],
                [username: 'admin', authority: MetadataRoles.ROLE_STACKTRACE],
                [username: 'admin', authority: MetadataRoles.ROLE_ADMIN],
                [username: 'curator', authority: MetadataRoles.ROLE_USER],
                [username: 'curator', authority: MetadataRoles.ROLE_CURATOR],
                [username: 'viewer', authority: MetadataRoles.ROLE_USER],]) {
            userRoleGormService.saveUserRoleByUsernameAndAuthority(m.username, m.authority)
        }
    }
}
