package org.modelcatalogue.core

import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.security.UserService
import org.modelcatalogue.core.util.LoggedUserUtils
import org.springframework.context.MessageSource

@CompileStatic
class ApiKeyController {

    UserService userService
    UserGormService userGormService

    MessageSource messageSource

    static allowedMethods = [
            index: 'GET',
            reset: 'POST'
    ]

    SpringSecurityService springSecurityService

    def index() {
        Object principal = springSecurityService.principal
        Long userId = LoggedUserUtils.id(principal) as Long
        String apiKey = userGormService.findApiKeyById(userId)
        [apiKey: apiKey]
    }

    @CompileDynamic
    def reset() {
        userService.resetApiKey(springSecurityService?.principal?.username)
        flash.message = messageSource.getMessage('user.apiKey.regenerated', [] as Object[], 'Api Key regenerated', request.locale)
        redirect controller: 'apiKey', action: 'index'
    }
}
