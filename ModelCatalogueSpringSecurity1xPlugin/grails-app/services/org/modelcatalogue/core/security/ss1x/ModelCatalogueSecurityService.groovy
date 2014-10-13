package org.modelcatalogue.core.security.ss1x

import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.modelcatalogue.core.SecurityService

class ModelCatalogueSecurityService implements SecurityService {

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
}
