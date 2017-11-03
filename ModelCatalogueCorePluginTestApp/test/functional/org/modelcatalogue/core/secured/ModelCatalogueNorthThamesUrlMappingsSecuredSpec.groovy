package org.modelcatalogue.core.secured

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class ModelCatalogueNorthThamesUrlMappingsSecuredSpec extends Specification {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "ModelCatalogueNorthThamesUrlMappings GET #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.get("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
                '/api/modelCatalogue/core/northThames/northThamesSummaryReport/$id',
        ]
    }
}
