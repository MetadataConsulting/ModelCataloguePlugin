package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

import static javax.servlet.http.HttpServletResponse.*

@TestFor(DataImportCreateController)
class DataImportCreateControllerAllowedMethodsSpec extends Specification {

    @Unroll
    def "test DataImportCreateController.importExcel does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.importExcel()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test DataImportCreateController.importExcel accepts GET requests"() {
        when:
        request.method = 'GET'
        controller.importExcel()

        then:
        response.status == SC_OK
    }

    @Unroll
    def "test DataImportCreateController.importObo does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.importObo()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test DataImportCreateController.importObo accepts GET requests"() {
        when:
        request.method = 'GET'
        controller.importObo()

        then:
        response.status == SC_OK
    }
}
