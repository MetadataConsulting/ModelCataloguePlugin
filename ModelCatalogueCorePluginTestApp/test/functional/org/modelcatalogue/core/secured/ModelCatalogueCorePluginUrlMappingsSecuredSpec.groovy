package org.modelcatalogue.core.secured

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class ModelCatalogueCorePluginUrlMappingsSecuredSpec extends Specification {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "ModelCatalogueCorePluginUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
                '/catalogue/upload',
                '/api/modelCatalogue/core/search/reindex',
                '/api/modelCatalogue/core/relationship/$id/restore',
        ]
    }

    @Unroll
    def "ModelCatalogueCorePluginUrlMappings GET #endpoint is NOT secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.get("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 200

        where:
        endpoint << [
                '/',
        ]
    }

    @Unroll
    def "ModelCatalogueCorePluginUrlMappings GET #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.get("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
        '/api/modelCatalogue/core/forms/generate/$id',
        '/api/modelCatalogue/core/forms/preview/$id',
        '/catalogue/ext/$key/$value',
        '/catalogue/ext/$key/$value/export',
        '/catalogue/$resource/$id',
        '/catalogue/$resource/$id/export',
        '/api/modelCatalogue/core/feedback',
        '/api/modelCatalogue/core/feedback/$key',
        '/api/modelCatalogue/core/logs',
        '/load',
        '/api/modelCatalogue/core/search/$search?',
                ]
    }
}
