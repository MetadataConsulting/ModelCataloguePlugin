package org.modelcatalogue.core.secured

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class MeasurementUnitUrlMappingsSecuredSpec extends Specification {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "MeasurementUnitUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
                '/api/modelCatalogue/core/measurementUnit',
                '/api/modelCatalogue/core/measurementUnit/$id/validate',
                '/api/modelCatalogue/core/measurementUnit/validate',
                '/api/modelCatalogue/core/measurementUnit/$id/archive',
                '/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type',
                '/api/modelCatalogue/core/measurementUnit/$id/restore',
                '/api/modelCatalogue/core/measurementUnit/$id/mapping/$destination',
                '/api/modelCatalogue/core/measurementUnit/$id/incoming/$type',
                '/api/modelCatalogue/core/measurementUnit/$id/clone/$destinationDataModelId',
                '/api/modelCatalogue/core/measurementUnit/$source/merge/$destination',
        ]
    }

    @Unroll
    def "MeasurementUnitUrlMappings GET #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.get("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
                '/api/modelCatalogue/core/measurementUnit',
                '/api/modelCatalogue/core/measurementUnit/search/$search',
                '/api/modelCatalogue/core/measurementUnit/$id',
                '/api/modelCatalogue/core/measurementUnit/$id/outgoing/search',
                '/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type/search',
                '/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type',
                '/api/modelCatalogue/core/measurementUnit/$id/incoming/search',
                '/api/modelCatalogue/core/measurementUnit/$id/incoming/$type/search',
                '/api/modelCatalogue/core/measurementUnit/$id/incoming/$type',
                '/api/modelCatalogue/core/measurementUnit/$id/incoming',
                '/api/modelCatalogue/core/measurementUnit/$id/outgoing',
                '/api/modelCatalogue/core/measurementUnit/$id/mapping',
                '/api/modelCatalogue/core/measurementUnit/$id/typeHierarchy',
                '/api/modelCatalogue/core/measurementUnit/$id/history',
                '/api/modelCatalogue/core/measurementUnit/$id/path',
                '/api/modelCatalogue/core/measurementUnit/$id/primitiveType',
        ]
    }

    @Unroll
    def "MeasurementUnitUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [

                '/api/modelCatalogue/core/measurementUnit/$id',
                '/api/modelCatalogue/core/measurementUnit/$id/incoming/$type',
                '/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type',
        ]
    }

    @Unroll
    def "MeasurementUnitUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
                '/api/modelCatalogue/core/measurementUnit/$id',
                '/api/modelCatalogue/core/measurementUnit/$id/mapping/$destination',
                '/api/modelCatalogue/core/measurementUnit/$id/incoming/$type',
                '/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type',
                ]
    }
}