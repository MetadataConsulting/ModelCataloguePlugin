package org.modelcatalogue.core.actions

import grails.test.mixin.TestFor
import org.modelcatalogue.core.mappingsuggestions.MappingSuggestionsController
import org.modelcatalogue.core.persistence.BatchGormService
import spock.lang.Specification
import spock.lang.Unroll

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED

@TestFor(BatchController)
class BatchControllerAllowedMethodsSpec extends Specification {

    @Unroll
    def "test BatchController.create does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.create()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test BatchController.create accepts GET requests"() {
        when:
        request.method = 'GET'
        controller.batchService = Mock(BatchService)
        controller.create()

        then:
        response.status == 200
    }

    @Unroll
    def "test BatchController.all does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.all()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test BatchController.all accepts GET requests"() {
        when:
        request.method = 'GET'
        controller.batchService = Mock(BatchService)
        controller.batchGormService = Mock(BatchGormService)
        controller.all()

        then:
        response.status == 200
    }

    @Unroll
    def "test BatchController.generateSuggestions does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.generateSuggestions()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'GET', 'PUT']
    }

    def "test BatchController.generateSuggestions accepts POST requests"() {
        when:
        request.method = 'POST'
        controller.generateSuggestions()

        then:
        response.status == 302
    }

    @Unroll
    def "test BatchController.archive does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.archive()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'GET', 'PUT']
    }

    def "test BatchController.archive accepts POST requests"() {
        when:
        request.method = 'POST'
        controller.archive()

        then:
        response.status == 302
    }
}
