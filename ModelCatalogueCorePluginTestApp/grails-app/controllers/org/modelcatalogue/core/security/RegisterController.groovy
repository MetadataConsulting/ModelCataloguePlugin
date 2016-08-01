package org.modelcatalogue.core.security

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.authentication.dao.NullSaltSource
import grails.plugin.springsecurity.ui.RegisterCommand
import grails.plugin.springsecurity.ui.RegistrationCode

class RegisterController extends grails.plugin.springsecurity.ui.RegisterController {

    @Override
    def register(RegisterCommand command) {
        if (!grailsApplication.config.grails.mc.allow.signup) {
            flash.error = "Registration is not enabled for this application"
            return
        }
        params.remove 'format'

        if (command.hasErrors()) {
            render view: 'index', model: [command: command]
            return
        }

        String salt = saltSource instanceof NullSaltSource ? null : command.username
        def user = lookupUserClass().newInstance(email: command.email, username: command.username,
                accountLocked: true, enabled: false)

        RegistrationCode registrationCode = springSecurityUiService.register(user, command.password, salt)
        if (registrationCode == null || registrationCode.hasErrors()) {
            // null means problem creating the user
            flash.error = message(code: 'spring.security.ui.register.miscError')
            flash.chainedParams = params
            redirect action: 'index'
            return
        }

        String url = generateLink('verifyRegistration', [t: registrationCode.token])

        def conf = SpringSecurityUtils.securityConfig
        def body = conf.ui.register.emailBody
        if (body.contains('$')) {
            body = evaluate(body, [user: user, url: url])
        }
        mailService.sendMail {
            to command.email
            from conf.ui.register.emailFrom
            subject conf.ui.register.emailSubject
            html body.toString()
        }

        render view: 'index', model: [emailSent: true]
    }



}
