package org.modelcatalogue.core.security

import grails.validation.Validateable

@Validateable
class RegisterResult {
    String email
    String username

    static constraints = {
        email nullable: false, blank: false
        username nullable: false, blank: false
    }
}
