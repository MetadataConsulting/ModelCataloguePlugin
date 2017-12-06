package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class EnumeratedTypeUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "EnumeratedTypeUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/enumeratedType/$id/archive',
                '/api/modelCatalogue/core/enumeratedType/$id/restore',
                '/api/modelCatalogue/core/enumeratedType/$id/clone/$destinationDataModelId',
                '/api/modelCatalogue/core/enumeratedType/$source/merge/$destination',
                '/api/modelCatalogue/core/enumeratedType',
                '/api/modelCatalogue/core/enumeratedType/$id/validate',
                '/api/modelCatalogue/core/enumeratedType/$id/setDeprecated',
                '/api/modelCatalogue/core/enumeratedType/validate',
                '/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type',
                '/api/modelCatalogue/core/enumeratedType/$id/incoming/$type',
                '/api/modelCatalogue/core/enumeratedType/$id/mapping/$destination',
        ]
    }

    @Unroll
    def "EnumeratedTypeUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
                '/api/modelCatalogue/core/enumeratedType',
                '/api/modelCatalogue/core/enumeratedType/search/$search',
                '/api/modelCatalogue/core/enumeratedType/$id',
                '/api/modelCatalogue/core/enumeratedType/$id/outgoing/search',
                '/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type/search',
                '/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type',
                '/api/modelCatalogue/core/enumeratedType/$id/incoming/search',
                '/api/modelCatalogue/core/enumeratedType/$id/incoming/$type/search',
                '/api/modelCatalogue/core/enumeratedType/$id/incoming/$type',
                '/api/modelCatalogue/core/enumeratedType/$id/incoming',
                '/api/modelCatalogue/core/enumeratedType/$id/outgoing',
                '/api/modelCatalogue/core/enumeratedType/$id/mapping',
                '/api/modelCatalogue/core/enumeratedType/$id/typeHierarchy',
                '/api/modelCatalogue/core/enumeratedType/$id/history',
                '/api/modelCatalogue/core/enumeratedType/$id/path',
                '/api/modelCatalogue/core/enumeratedType/$id/dataElement',
                '/api/modelCatalogue/core/enumeratedType/$id/convert/$destination',
                '/api/modelCatalogue/core/enumeratedType/$id/validateValue',
                '/api/modelCatalogue/core/enumeratedType/$id/content',
        ]
    }

    @Unroll
    def "EnumeratedTypeUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/enumeratedType/$id',
                '/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type',
                '/api/modelCatalogue/core/enumeratedType/$id/incoming/$type',
        ]
    }

    @Unroll
    def "EnumeratedTypeUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/enumeratedType/$id',
                '/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type',
                '/api/modelCatalogue/core/enumeratedType/$id/incoming/$type',
                '/api/modelCatalogue/core/enumeratedType/$id/mapping/$destination',
        ]
    }
}