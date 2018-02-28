package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED
import static javax.servlet.http.HttpServletResponse.SC_OK

@TestFor(ModelCatalogueVersionController)
class ModelCatalogueVersionControllerAllowedMethodsSpec extends Specification {

    @Unroll
    def "test ModelCatalogueVersionController.index does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.index()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test ModelCatalogueVersionController.index accepts GET requests"() {
        when:
        request.method = 'GET'
        controller.index()

        then:
        response.status == SC_OK
    }
}
