package org.modelcatalogue.core

import grails.plugin.springsecurity.ui.RegistrationCode
import grails.transaction.Transactional
import org.modelcatalogue.core.persistence.RegistrationCodeGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole

class RegisterService {

    UserGormService userGormService
    UserRoleGormService userRoleGormService
    RegistrationCodeGormService registrationCodeGormService

    List<String> DEFAULT_AUTHORITES_GRANTED = [MetadataRoles.ROLE_USER]

    @Transactional
    RegistrationCode register(String username, String password, String email, boolean enabled = true) {
        User user = new User(username: username,
                password: password,
                email: email,
                enabled: enabled,
                accountExpired: false,
                accountLocked: false,
                passwordExpired: false)
        register(user)
    }

    @Transactional
    RegistrationCode register(User user) {

        User u = userGormService.save(user)
        if ( u.hasErrors() ) {
            return null
        }
        for ( String authority : DEFAULT_AUTHORITES_GRANTED ) {
            UserRole userRole = userRoleGormService.saveUserRoleByUsernameAndAuthority(u.username, authority)
            if ( userRole.hasErrors() ) {
                return null
            }
        }
        registrationCodeGormService.saveWithUsername(u.username)
    }
}
