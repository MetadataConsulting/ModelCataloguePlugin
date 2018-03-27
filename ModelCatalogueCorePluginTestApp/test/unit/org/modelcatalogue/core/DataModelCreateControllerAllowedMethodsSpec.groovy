package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED
import static javax.servlet.http.HttpServletResponse.SC_OK

@TestFor(DataModelCreateController)
class DataModelCreateControllerAllowedMethodsSpec extends Specification {

    @Unroll
    def "test DataImportCreateController.create does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.create()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test DataImportCreateController.create accepts GET requests"() {
        given:
        controller.dataModelCreateService = Mock(DataModelCreateService)

        when:
        request.method = 'GET'
        controller.create()

        then:
        response.status == SC_OK
    }

    @Unroll
    def "test DataImportCreateController.save does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.save()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'GET', 'PUT']
    }

    def "test DataImportCreateController.save accepts POST requests"() {
        given:
        controller.dataModelCreateService = Mock(DataModelCreateService)

        when:
        request.method = 'POST'
        controller.save()

        then:
        response.status == SC_OK
    }
}
