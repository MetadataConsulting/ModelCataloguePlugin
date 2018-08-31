package org.modelcatalogue.core.security

import org.modelcatalogue.core.RegisterService
import org.modelcatalogue.core.validation.ValidationErrorsJsonView
import org.springframework.context.MessageSource

class ApiRegisterController {

    static responseFormats = ['json']

    static allowedMethods = [register: 'POST']

    MessageSource messageSource

    RegisterService registerService

    def register(ApiRegisterCommand command) {
        if (command.hasErrors()) {
            respond ValidationErrorsJsonView.of(command.errors, messageSource, request.locale)
            response.setStatus(422)
            return
        }
        RegisterResult registerResult = registerService.register(command)

        if (registerResult.hasErrors()) {
            respond ValidationErrorsJsonView.of(registerResult.errors, messageSource, request.locale)
            response.setStatus(422)
            return
        }
        response.setStatus(200)
        respond([:])
    }

}
