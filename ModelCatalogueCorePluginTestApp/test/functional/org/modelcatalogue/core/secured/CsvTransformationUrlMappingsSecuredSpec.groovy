package org.modelcatalogue.core.secured

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class CsvTransformationUrlMappingsSecuredSpec extends Specification {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "CsvTransformationUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
            '/api/modelCatalogue/core/csvTransformation',
            '/api/modelCatalogue/core/csvTransformation/$id/validate',
            '/api/modelCatalogue/core/csvTransformation/validate',
            '/api/modelCatalogue/core/csvTransformation/$id/transform',
        ]
    }

    @Unroll
    def "CsvTransformationUrlMappings GET #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.get("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
                '/api/modelCatalogue/core/csvTransformation',
                '/api/modelCatalogue/core/csvTransformation/search/$search',
                '/api/modelCatalogue/core/csvTransformation/$id',
        ]
    }

    @Unroll
    def "CsvTransformationUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
            '/api/modelCatalogue/core/csvTransformation/$id',
        ]
    }

    @Unroll
    def "CsvTransformationUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
           '/api/modelCatalogue/core/csvTransformation/$id',
        ]
    }
}

