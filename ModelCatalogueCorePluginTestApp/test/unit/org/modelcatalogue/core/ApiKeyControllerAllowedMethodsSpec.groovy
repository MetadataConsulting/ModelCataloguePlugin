package org.modelcatalogue.core

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import org.modelcatalogue.core.security.UserService
import spock.lang.Specification
import spock.lang.Unroll
import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED
import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY

@TestFor(ApiKeyController)
class ApiKeyControllerAllowedMethodsSpec extends Specification {

    @Unroll
    def "test ApiKeyController.index does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.index()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test ApiKeyController.index accepts GET requests"() {
        given:
        controller.springSecurityService = Mock(SpringSecurityService)
        controller.userService = Mock(UserService)

        when:
        request.method = 'GET'
        controller.index()

        then:
        response.status == SC_OK
    }

    @Unroll
    def "test ApiKeyController.reset does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.reset()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'GET', 'PUT']
    }

    def "test ApiKeyController.reset accepts GET requests"() {
        given:
        controller.userService = Mock(UserService)
        controller.springSecurityService = Mock(SpringSecurityService)

        when:
        request.method = 'POST'
        controller.reset()

        then:
        response.status == SC_MOVED_TEMPORARILY
    }
}
