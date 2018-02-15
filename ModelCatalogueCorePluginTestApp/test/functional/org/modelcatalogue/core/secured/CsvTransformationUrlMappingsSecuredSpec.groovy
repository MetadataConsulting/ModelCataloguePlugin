package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Unroll
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class CsvTransformationUrlMappingsSecuredSpec extends GebSpec {

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
        response.status == 302

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
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

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
        response.status == 302

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
        response.status == 302

        where:
        endpoint << [
           '/api/modelCatalogue/core/csvTransformation/$id',
        ]
    }
}

