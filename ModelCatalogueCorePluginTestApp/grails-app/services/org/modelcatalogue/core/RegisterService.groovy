package org.modelcatalogue.core

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.ui.RegistrationCode
import grails.transaction.Transactional
import groovy.text.SimpleTemplateEngine
import groovy.util.logging.Slf4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.core.persistence.RegistrationCodeGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.RegisterRequest
import org.modelcatalogue.core.security.RegisterResult
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole
import org.modelcatalogue.core.security.UserService

import javax.annotation.PostConstruct

@Slf4j
class RegisterService {

    UserGormService userGormService
    UserRoleGormService userRoleGormService
    RegistrationCodeGormService registrationCodeGormService
    MaxActiveUsersService maxActiveUsersService
    LinkGenerator grailsLinkGenerator
    GrailsApplication grailsApplication
    List<String> DEFAULT_AUTHORITES_GRANTED = [MetadataRoles.ROLE_USER]
    TransactionalEmailService transactionalEmailService
    String supervisorEmail

    @PostConstruct
    void initialize() {
        supervisorEmail = System.getenv(UserService.ENV_SUPERVISOR_EMAIL)
    }

    @Transactional
    RegistrationCode register(String username, String password, String email, boolean enabled = true) {
        if ( maxActiveUsersService.maxActiveUsers() ) {
            if ( maxActiveUsersService.maxActiveUsers() ) {
                log.info 'Limit of {} users has been reached', maxActiveUsersService.maxUsers
                return
            }

            return null
        }
        User user = new User(username: username,
                password: password,
                email: email,
                enabled: enabled,
                accountExpired: false,
                accountLocked: false,
                passwordExpired: false)
        register(user)
    }

    @Transactional
    RegistrationCode register(User user) {
        if ( maxActiveUsersService.maxActiveUsers() ) {
            log.info 'Limit of {} users has been reached', maxActiveUsersService.maxUsers
            return null
        }

        User u = userGormService.save(user)
        if ( u.hasErrors() ) {
            return null
        }
        for ( String authority : DEFAULT_AUTHORITES_GRANTED ) {
            UserRole userRole = userRoleGormService.saveUserRoleByUsernameAndAuthority(u.username, authority)
            if ( userRole.hasErrors() ) {
                return null
            }
        }
        registrationCodeGormService.saveWithUsername(u.username)
    }


    protected boolean shouldUserBeDisabled(String email) {
        (supervisorEmail && email != supervisorEmail)
    }

    protected void notifySupervisorAboutNewDisableduser(String email) {
        if ( shouldUserBeDisabled(email) ) {
            // notify admin
            transactionalEmailService.sendEmail(supervisorEmail,
                    conf.ui.register.emailFrom,
                    "Metadata Registry - new user",
                    "New user registered to your Metadata Registry. Please enable that account in user administration.")
        }
    }

    RegisterResult register(RegisterRequest request) {
        RegisterResult registerResult = new RegisterResult(username: request.username, email: request.email)
        if (userGormService.findByUsername(request.username)) {
            registerResult.errors.rejectValue('username', 'username.not.unique.message', 'Username not unique')
            return registerResult
        }

        if (userGormService.findByEmail(request.email)) {
            registerResult.errors.rejectValue('email', 'email.not.unique.message', 'Email not unique')
            return registerResult
        }

        boolean shouldUserBeDisabled = shouldUserBeDisabled(request.email)
        if (shouldUserBeDisabled) {
            notifySupervisorAboutNewDisableduser(request.email)
        }
        boolean enabled = !shouldUserBeDisabled
        RegistrationCode registrationCode = register(request.username, request.password, request.email, enabled)
        if (registrationCode == null || registrationCode.hasErrors()) {
            registerResult.errors.reject('spring.security.ui.register.miscError')
            return registerResult
        }

        sendVerificationEmail(registrationCode.token, request)
        registerResult
    }

    protected void sendVerificationEmail(String token, RegisterRequest request) {
        String url = generateLink('verifyRegistration', [t: token])

        def conf = SpringSecurityUtils.securityConfig
        String body = conf.ui.register.emailBody
        if (body.contains('$')) {
            body = evaluate(body, [user: request, url: url])
        }

        transactionalEmailService.sendEmail(request.email,
                conf.ui.register.emailFrom as String,
                conf.ui.register.emailSubject as String,
                body.toString())
    }

    protected String generateLink(String action, linkParams) {
        grailsLinkGenerator.link(base: grailsApplication.config.grails.serverURL,
                controller: 'register', action: action,
                params: linkParams)
    }

    protected String evaluate(String s, Map binding) {
        new SimpleTemplateEngine().createTemplate(s).make(binding)
    }
}
