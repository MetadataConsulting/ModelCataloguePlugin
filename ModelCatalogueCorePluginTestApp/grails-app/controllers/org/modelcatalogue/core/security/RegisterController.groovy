package org.modelcatalogue.core.security

import grails.plugin.springsecurity.ui.RegisterCommand

class RegisterController extends grails.plugin.springsecurity.ui.RegisterController {

    @Override
    def register(RegisterCommand command) {
        if (!grailsApplication.config.grails.mc.allow.signup) {
            flash.error = "Registration is not enabled for this application"
            return
        }
        params.remove 'format'
        return super.register(command)
    }
}
