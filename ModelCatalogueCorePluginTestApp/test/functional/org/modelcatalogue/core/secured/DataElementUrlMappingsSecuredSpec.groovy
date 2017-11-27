package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class DataElementUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "DataElementUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataElement',
        '/api/modelCatalogue/core/dataElement/$id/validate',
        '/api/modelCatalogue/core/dataElement/validate',
        '/api/modelCatalogue/core/dataElement/$id/restore',
        '/api/modelCatalogue/core/dataElement/$id/clone/$destinationDataModelId',
        '/api/modelCatalogue/core/dataElement/$source/merge/$destination',
        '/api/modelCatalogue/core/dataElement/$id/archive',
        '/api/modelCatalogue/core/dataElement/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataElement/$id/incoming/$type',
        '/api/modelCatalogue/core/dataElement/$id/mapping/$destination',
                ]
    }

    @Unroll
    def "DataElementUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataElement',
        '/api/modelCatalogue/core/dataElement/search/$search',
        '/api/modelCatalogue/core/dataElement/$id',
        '/api/modelCatalogue/core/dataElement/$id/outgoing/search',
        '/api/modelCatalogue/core/dataElement/$id/outgoing/$type/search',
        '/api/modelCatalogue/core/dataElement/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataElement/$id/incoming/search',
        '/api/modelCatalogue/core/dataElement/$id/incoming/$type/search',
        '/api/modelCatalogue/core/dataElement/$id/incoming/$type',
        '/api/modelCatalogue/core/dataElement/$id/incoming',
        '/api/modelCatalogue/core/dataElement/$id/outgoing',
        '/api/modelCatalogue/core/dataElement/$id/mapping',
        '/api/modelCatalogue/core/dataElement/$id/typeHierarchy',
        '/api/modelCatalogue/core/dataElement/$id/history',
        '/api/modelCatalogue/core/dataElement/$id/path',
        '/api/modelCatalogue/core/dataElement/$id/content',
                ]
    }

    @Unroll
    def "DataElementUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataElement/$id',
        '/api/modelCatalogue/core/dataElement/$id/incoming/$type',
        '/api/modelCatalogue/core/dataElement/$id/outgoing/$type',
        ]

    }

    @Unroll
    def "DataElementUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataElement/$id/incoming/$type',
        '/api/modelCatalogue/core/dataElement/$id',
        '/api/modelCatalogue/core/dataElement/$id/mapping/$destination',
        '/api/modelCatalogue/core/dataElement/$id/outgoing/$type',
                ]

    }
}
