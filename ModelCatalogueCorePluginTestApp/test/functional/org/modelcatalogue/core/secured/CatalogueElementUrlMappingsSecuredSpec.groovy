package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class CatalogueElementUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "CatalogueElementUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/catalogueElement',
        '/api/modelCatalogue/core/catalogueElement/$id/validate',
        '/api/modelCatalogue/core/catalogueElement/validate',
        '/api/modelCatalogue/core/catalogueElement/$id/archive',
        '/api/modelCatalogue/core/catalogueElement/$id/restore',
        '/api/modelCatalogue/core/catalogueElement/$id/clone/$destinationDataModelId',
        '/api/modelCatalogue/core/catalogueElement/$source/merge/$destination',
        '/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type',
        '/api/modelCatalogue/core/catalogueElement/$id/incoming/$type',
        '/api/modelCatalogue/core/catalogueElement/$id/mapping/$destination',
                ]
    }

    @Unroll
    def "CatalogueElementUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
        '/api/modelCatalogue/core/catalogueElement',
        '/api/modelCatalogue/core/catalogueElement/search/$search',
        '/api/modelCatalogue/core/catalogueElement/$id',
        '/api/modelCatalogue/core/catalogueElement/$id/outgoing/search',
        '/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type/search',
        '/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type',
        '/api/modelCatalogue/core/catalogueElement/$id/incoming/search',
        '/api/modelCatalogue/core/catalogueElement/$id/incoming/$type/search',
        '/api/modelCatalogue/core/catalogueElement/$id/incoming/$type',
        '/api/modelCatalogue/core/catalogueElement/$id/incoming',
        '/api/modelCatalogue/core/catalogueElement/$id/outgoing',
        '/api/modelCatalogue/core/catalogueElement/$id/mapping',
        '/api/modelCatalogue/core/catalogueElement/$id/typeHierarchy',
        '/api/modelCatalogue/core/catalogueElement/$id/history',
        '/api/modelCatalogue/core/catalogueElement/$id/path',
                ]
    }

    @Unroll
    def "CatalogueElementUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/catalogueElement/$id',
        '/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type',
        '/api/modelCatalogue/core/catalogueElement/$id/incoming/$type',
                ]
    }

    @Unroll
    def "CatalogueElementUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/catalogueElement/$id',
        '/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type',
        '/api/modelCatalogue/core/catalogueElement/$id/mapping/$destination',
        '/api/modelCatalogue/core/catalogueElement/$id/incoming/$type',
                ]
    }
}
