package org.modelcatalogue.core.security

import grails.plugin.springsecurity.ui.RegisterCommand
import org.modelcatalogue.core.RegisterService
import org.modelcatalogue.core.persistence.UserGormService

class ApiRegisterController {

    static responseFormats = ['json']

    static allowedMethods = [register: 'POST']

    RegisterService registerService

    def register(ApiRegisterCommand command) {
        if (command.hasErrors()) {
            respond command
            response.setStatus(422)
            return
        }
        RegisterResult registerResult = registerService.register(command)

        if (registerResult.hasErrors()) {
            respond registerResult
            response.setStatus(422)
            return
        }
        response.setStatus(200)
        respond([:])
    }

}
