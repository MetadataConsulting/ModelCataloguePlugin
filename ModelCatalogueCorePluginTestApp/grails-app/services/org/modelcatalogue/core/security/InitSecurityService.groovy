package org.modelcatalogue.core.security

class InitSecurityService {

    RoleGormService roleGormService

    UserGormService userGormService

    UserRoleGormService userRoleGormService

    /**
     * @description if the MetadataRoles don't exist in the database it saves them
     */
    void initRoles() {
        [
                MetadataRoles.ROLE_USER,
                MetadataRoles.ROLE_ADMIN,
                MetadataRoles.ROLE_SUPERVISOR,
                MetadataRoles.ROLE_STACKTRACE,
                MetadataRoles.ROLE_CURATOR,
                MetadataRoles.ROLE_REGISTERED
        ].each { String authority ->
            if ( !roleGormService.findByAuthority(authority) ) {
                roleGormService.saveByAuthority(authority)
            }
        }
    }

    void initUsers() {
        [
                [username: 'supervisor', password: System.getenv('MC_SUPERVISOR_PASSWORD') ?: 'supervisor', email: System.getenv(UserService.ENV_SUPERVISOR_EMAIL), apiKey: 'supervisorabcdef123456'],
                [username: 'admin', password: 'admin', email: System.getenv('MC_ADMIN_EMAIL'), apiKey: 'adminabcdef123456'],
                [username: 'viewer', password: 'viewer', apiKey: 'viewerabcdef123456'],
                [username: 'curator', password: 'curator', apiKey: 'curatorabcdef123456'],
                [username: 'registered', password: 'registered', apiKey: 'registeredabcdef123456'],

        ].each { Map m ->
            if ( !userGormService.findByNameOrUsername(m.username, m.username) ) {
                User user = new User(name: 'supervisor', username: 'supervisor', enabled: true, password: m.password, email: m.email, apiKey: m.apiKey)
                userGormService.save(user)
            }
        }

        initUserRoles()
    }

    void initUserRoles() {
        userRoleGormService.saveUserRoleByUsernameAndAuthority('supervisor', MetadataRoles.ROLE_USER)
        userRoleGormService.saveUserRoleByUsernameAndAuthority('supervisor', MetadataRoles.ROLE_CURATOR)
        userRoleGormService.saveUserRoleByUsernameAndAuthority('supervisor', MetadataRoles.ROLE_STACKTRACE)
        userRoleGormService.saveUserRoleByUsernameAndAuthority('supervisor', MetadataRoles.ROLE_ADMIN)
        userRoleGormService.saveUserRoleByUsernameAndAuthority('supervisor', MetadataRoles.ROLE_SUPERVISOR)

        userRoleGormService.saveUserRoleByUsernameAndAuthority('admin', MetadataRoles.ROLE_USER)
        userRoleGormService.saveUserRoleByUsernameAndAuthority('admin', MetadataRoles.ROLE_CURATOR)
        userRoleGormService.saveUserRoleByUsernameAndAuthority('admin', MetadataRoles.ROLE_STACKTRACE)
        userRoleGormService.saveUserRoleByUsernameAndAuthority('admin', MetadataRoles.ROLE_ADMIN)

        userRoleGormService.saveUserRoleByUsernameAndAuthority('curator', MetadataRoles.ROLE_USER)
        userRoleGormService.saveUserRoleByUsernameAndAuthority('curator', MetadataRoles.ROLE_CURATOR)

        userRoleGormService.saveUserRoleByUsernameAndAuthority('viewer', MetadataRoles.ROLE_USER)
    }
}
