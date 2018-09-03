package org.modelcatalogue.core.security

import grails.validation.Validateable

@Validateable
class ApiRegisterCommand implements RegisterRequest {
    String username
    String email
    String password

    static constraints = {
        username nullable: false, blank: false
        email nullable: false, blank: false
        password nullable: false, blank: false, validator: RegisterController.passwordValidator
    }
}
