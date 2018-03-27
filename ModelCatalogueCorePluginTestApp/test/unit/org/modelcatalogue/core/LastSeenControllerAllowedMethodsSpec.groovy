package org.modelcatalogue.core

import grails.test.mixin.TestFor
import org.modelcatalogue.core.persistence.UserAuthenticationGormService
import spock.lang.Specification
import spock.lang.Unroll

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED
import static javax.servlet.http.HttpServletResponse.SC_OK

@TestFor(LastSeenController)
class LastSeenControllerAllowedMethodsSpec extends Specification {

    @Unroll
    def "test LastSeenController.index does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.index()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test LastSeenController.index accepts GET requests"() {
        given:
        controller.userAuthenticationGormService = Mock(UserAuthenticationGormService)

        when:
        request.method = 'GET'
        controller.index()

        then:
        response.status == SC_OK
    }
}
