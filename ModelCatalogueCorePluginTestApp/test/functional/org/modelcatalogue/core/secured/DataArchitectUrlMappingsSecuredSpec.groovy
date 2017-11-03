package org.modelcatalogue.core.secured

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class DataArchitectUrlMappingsSecuredSpec extends Specification {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "DataArchitectUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataArchitect/elementsFromCSV',
        '/api/modelCatalogue/core/dataArchitect/modelsFromCSV',
        '/api/modelCatalogue/core/dataArchitect/generateSuggestions',
        '/api/modelCatalogue/core/dataArchitect/deleteSuggestions',
        '/api/modelCatalogue/core/dataArchitect/imports/upload',
                ]
    }

    @Unroll
    def "DataArchitectUrlMappings GET #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.get("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataArchitect/metadataKeyCheck/$key',
        '/api/modelCatalogue/core/dataArchitect/getSubModelElements/$modelId',
        '/api/modelCatalogue/core/dataArchitect/findRelationsByMetadataKeys/$key',
        '/api/modelCatalogue/core/dataArchitect/suggestionsNames',
        ]
    }
}
