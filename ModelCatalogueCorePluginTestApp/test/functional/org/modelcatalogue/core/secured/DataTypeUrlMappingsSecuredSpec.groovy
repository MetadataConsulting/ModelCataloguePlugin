package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class DataTypeUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "DataTypeUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataType',
        '/api/modelCatalogue/core/dataType/$id/validate',
        '/api/modelCatalogue/core/dataType/validate',
        '/api/modelCatalogue/core/dataType/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataType/$id/incoming/$type',
        '/api/modelCatalogue/core/dataType/$id/mapping/$destination',
        '/api/modelCatalogue/core/dataType/$id/archive',
        '/api/modelCatalogue/core/dataType/$id/restore',
        '/api/modelCatalogue/core/dataType/$id/clone/$destinationDataModelId',
        '/api/modelCatalogue/core/dataType/$source/merge/$destination',
                ]
    }

    @Unroll
    def "DataTypeUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataType',
        '/api/modelCatalogue/core/dataType/search/$search',
        '/api/modelCatalogue/core/dataType/$id',
        '/api/modelCatalogue/core/dataType/$id/outgoing/search',
        '/api/modelCatalogue/core/dataType/$id/outgoing/$type/search',
        '/api/modelCatalogue/core/dataType/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataType/$id/incoming/search',
        '/api/modelCatalogue/core/dataType/$id/incoming/$type/search',
        '/api/modelCatalogue/core/dataType/$id/incoming/$type',
        '/api/modelCatalogue/core/dataType/$id/incoming',
        '/api/modelCatalogue/core/dataType/$id/outgoing',
        '/api/modelCatalogue/core/dataType/$id/mapping',
        '/api/modelCatalogue/core/dataType/$id/typeHierarchy',
        '/api/modelCatalogue/core/dataType/$id/history',
        '/api/modelCatalogue/core/dataType/$id/path',
        '/api/modelCatalogue/core/dataType/$id/dataElement',
        '/api/modelCatalogue/core/dataType/$id/convert/$destination',
        '/api/modelCatalogue/core/dataType/$id/validateValue',
                ]
    }

    @Unroll
    def "DataTypeUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataType/$id',
        '/api/modelCatalogue/core/dataType/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataType/$id/incoming/$type',
                ]
    }

    @Unroll
    def "DataTypeUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataType/$id',
        '/api/modelCatalogue/core/dataType/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataType/$id/incoming/$type',
        '/api/modelCatalogue/core/dataType/$id/mapping/$destination',
                ]
    }
}

