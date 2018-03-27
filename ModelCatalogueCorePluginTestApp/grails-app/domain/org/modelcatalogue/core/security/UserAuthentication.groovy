package org.modelcatalogue.core.security

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class UserAuthentication {
    String username
    Date authenticationDate

    static constraints = {
        username nullable: false
        authenticationDate nullable: false
    }

    static mapping = {
        sort authenticationDate: 'desc'
    }
}
