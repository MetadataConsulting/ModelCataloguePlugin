package org.modelcatalogue.core.security

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.ui.RegisterCommand
import grails.plugin.springsecurity.ui.RegistrationCode
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.RegisterService
import org.modelcatalogue.core.TransactionalEmailService

/**
 * Extends RegisterController for shared methods.
 */
class ApiRegisterController extends RegisterController {

    static allowedMethods = [register: 'POST']

    /**
     * Returns JSON {
     *     ?flash: Flash,
     *     ?model: Model
     * }
     * type Flash = {
     *     error: string,
     *     ?chainedParams: Map<String, String>
     * }
     * type Model = {
     *     ?command: RegisterCommand,
     *     ?emailSent: boolean
     * }
     * @param command
     * @return
     */
    def register(RegisterCommand command) {
        def response = ["flash":["error": null, "chainedParams": [:]],
                        "model": ["command": null, "emailSent": null]]
        if (!grailsApplication.config.mc.allow.signup) {
            response.get("flash").putAt("error", "Registration is not enabled for this application")
            respond response
            return
        }
        params.remove 'format'

        if (command.hasErrors()) {
            response.get("model").putAt("command", command)
            respond response
            return
        }

        def supervisorEmail = System.getenv(UserService.ENV_SUPERVISOR_EMAIL)
        boolean shouldUserBeDisabled = (supervisorEmail && command.email != supervisorEmail)
        boolean enabled = !shouldUserBeDisabled
        if ( shouldUserBeDisabled ) {
            // notify admin
            transactionalEmailService.sendEmail(supervisorEmail,
                conf.ui.register.emailFrom,
                "Metadata Registry - new user",
                "New user registered to your Metadata Registry. Please enable that account in user administration.")
        }

        RegistrationCode registrationCode = registerService.register(command.username, command.password, command.email, enabled)
        if (registrationCode == null || registrationCode.hasErrors()) {
            // null means problem creating the user
            response = ["flash": ["error": message(code: 'spring.security.ui.register.miscError'),
                                    "chainedParams": params]]
            respond response
            return
        }

        String url = generateLink('verifyRegistration', [t: registrationCode.token])


        def conf = SpringSecurityUtils.securityConfig
        def body = conf.ui.register.emailBody
        if (body.contains('$')) {
            body = evaluate(body, [user: command, url: url])
        }

        transactionalEmailService.sendEmail(command.email,
            conf.ui.register.emailFrom as String,
            conf.ui.register.emailSubject as String,
            body.toString())

        response.get("model").put("emailSent", true)
        respond response
        return
    }

}
