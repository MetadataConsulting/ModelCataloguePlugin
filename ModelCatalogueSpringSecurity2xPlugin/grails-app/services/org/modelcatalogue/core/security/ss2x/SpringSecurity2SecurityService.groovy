package org.modelcatalogue.core.security.ss2x

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.security.User

class SpringSecurity2SecurityService implements SecurityService {

    static transactional = false

    SpringSecurityService springSecurityService

    boolean isUserLoggedIn() {
        return springSecurityService.isLoggedIn()
    }

    boolean hasRole(String role) {
        String translated = role
        if (role == "VIEWER") {
            translated = "ROLE_USER,ROLE_METADATA_CURATOR,ROLE_ADMIN"
        }  else if (role == "CURATOR") {
            translated = "ROLE_METADATA_CURATOR,ROLE_ADMIN"
        } else if (role == "ADMIN") {
            translated = "ROLE_ADMIN"
        } else if (!translated.startsWith('ROLE_')) {
            translated = "ROLE_${translated}"
        }
        return SpringSecurityUtils.ifAnyGranted(translated)
    }

    String encodePassword(String password) {
        return springSecurityService.encodePassword(password)
    }

    User getCurrentUser() {
        return springSecurityService.currentUser as User
    }
}
