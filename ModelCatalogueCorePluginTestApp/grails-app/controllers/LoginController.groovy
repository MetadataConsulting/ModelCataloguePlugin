import grails.converters.JSON
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

import javax.servlet.http.HttpServletResponse

import grails.plugin.springsecurity.SpringSecurityUtils

import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class LoginController {

    /**
     * Dependency injection for the authenticationTrustResolver.
     */
    def authenticationTrustResolver

    /**
     * Dependency injection for the springSecurityService.
     */
    def springSecurityService

    /**
     * Default action; redirects to 'defaultTargetUrl' if logged in, /login/auth otherwise.
     */
    def index = {
        if (springSecurityService.isLoggedIn()) {
            redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
        } else {
            redirect action: 'auth', params: params
        }
    }

    /**
     * Show the login page.
     */
    def auth = {
        // This solves the problem with infinite redirect loop when accessing main url witout trailing slash.
        // This is super dirty hack which actually redirects this request to trailing slash - ajax login.
        if (SpringSecurityUtils.getSavedRequest(session).redirectUrl
            && !grailsApplication.config.grails.serverURL.endsWith('/')
            && SpringSecurityUtils.getSavedRequest(session).redirectUrl == grailsApplication.config.grails.serverURL
        ) {
            redirect uri: "${SpringSecurityUtils.getSavedRequest(session).redirectUrl}/"
            return
        }

        def config = SpringSecurityUtils.securityConfig

        if (springSecurityService.isLoggedIn()) {
            redirect uri: config.successHandler.defaultTargetUrl
            return
        }

        String view = 'auth'
        String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
        response.setHeader('X-S2Auth-PostUrl', postUrl)
        render view: view, model: [postUrl            : postUrl,
                                   rememberMeParameter: config.rememberMe.parameter]
    }

    /**
     * The redirect action for Ajax requests.
     */
    def authAjax = {
        response.setHeader 'Location', SpringSecurityUtils.securityConfig.auth.ajaxLoginFormUrl
        response.sendError HttpServletResponse.SC_UNAUTHORIZED
    }

    /**
     * Show denied page.
     */
    def denied = {
        response.sendError HttpServletResponse.SC_UNAUTHORIZED
    }

    /**
     * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
     */
    def full = {
        def config = SpringSecurityUtils.securityConfig
        render view: 'auth', params: params,
                model: [hasCookie: authenticationTrustResolver.isRememberMe(SCH.context?.authentication),
                        postUrl  : "${request.contextPath}${config.apf.filterProcessesUrl}"]
    }

    /**
     * Callback after a failed login. Redirects to the auth page with a warning message.
     */
    def authfail = {
        String msg = ''
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
        if (exception) {
            if (exception instanceof AccountExpiredException) {
                msg = g.message(code: "springSecurity.errors.login.expired")
            } else if (exception instanceof CredentialsExpiredException) {
                msg = g.message(code: "springSecurity.errors.login.passwordExpired")
            } else if (exception instanceof DisabledException) {
                msg = g.message(code: "springSecurity.errors.login.disabled")
            } else if (exception instanceof LockedException) {
                msg = g.message(code: "springSecurity.errors.login.locked")
            } else {
                msg = g.message(code: "springSecurity.errors.login.fail")
            }
        }

        if (springSecurityService.isAjax(request)) {
            render([error: msg] as JSON)
        } else {
            flash.message = msg
            redirect action: 'auth', params: params
        }
    }

    /**
     * The Ajax success redirect url.
     *
     * You need to return roles array in this result to make security.hasRole angular service working
     *
     *
     */
    def ajaxSuccess = {
        if (!springSecurityService.currentUser) {
            render([success: false] as JSON)
            return
        }
        render([
            success: true,
            username: springSecurityService.authentication.name,
            roles: springSecurityService.authentication.authorities*.authority,
            id: springSecurityService.authentication.hasProperty('id') ? springSecurityService.authentication.id : null,
            dataModels: springSecurityService.authentication.hasProperty('id') ? [springSecurityService.authentication.dataModel?.collect{ CatalogueElementMarshaller.minimalCatalogueElementJSON(it) }] : []
        ] as JSON)
    }

    /**
     * The Ajax denied redirect url.
     */
    def ajaxDenied = {
        response.sendError HttpServletResponse.SC_UNAUTHORIZED
        render([error: 'access denied'] as JSON)
    }
}
