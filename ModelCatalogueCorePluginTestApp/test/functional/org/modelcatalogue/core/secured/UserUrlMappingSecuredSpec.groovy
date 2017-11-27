package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class UserUrlMappingSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "UserUrlMapping GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
        '/api/modelCatalogue/core/user',
        '/api/modelCatalogue/core/user/search/$search',
        '/api/modelCatalogue/core/user/$id',
        '/api/modelCatalogue/core/user/$id/outgoing/search',
        '/api/modelCatalogue/core/user/$id/outgoing/$type/search',
        '/api/modelCatalogue/core/user/$id/outgoing/$type',
        '/api/modelCatalogue/core/user/$id/incoming/search',
        '/api/modelCatalogue/core/user/$id/incoming/$type/search',
        '/api/modelCatalogue/core/user/$id/incoming/$type',
        '/api/modelCatalogue/core/user/$id/incoming',
        '/api/modelCatalogue/core/user/$id/outgoing',
        '/api/modelCatalogue/core/user/$id/mapping',
        '/api/modelCatalogue/core/user/$id/typeHierarchy',
        '/api/modelCatalogue/core/user/$id/history',
        '/api/modelCatalogue/core/user/$id/path',
        '/api/modelCatalogue/core/user/current',
        '/api/modelCatalogue/core/user/lastSeen'
        ]
    }

    @Unroll
    def "UserUrlMapping POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
            '/api/modelCatalogue/core/user',
            '/api/modelCatalogue/core/user/$id/validate',
            '/api/modelCatalogue/core/user/validate',
            '/api/modelCatalogue/core/user/$id/outgoing/$type',
            '/api/modelCatalogue/core/user/$id/incoming/$type',
            '/api/modelCatalogue/core/user/$id/mapping/$destination',
            '/api/modelCatalogue/core/user/$id/archive',
            '/api/modelCatalogue/core/user/$id/restore',
            '/api/modelCatalogue/core/user/$source/merge/$destination',
            '/api/modelCatalogue/core/user/classifications',
            '/api/modelCatalogue/core/user/apikey',
            '/api/modelCatalogue/core/user/$id/favourite',
            '/api/modelCatalogue/core/user/$id/enable',
            '/api/modelCatalogue/core/user/$id/disable',
            '/api/modelCatalogue/core/user/$id/role/$role',
        ]
    }

    @Unroll
    def "UserUrlMapping PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
            '/api/modelCatalogue/core/user/$id',
            '/api/modelCatalogue/core/user/$id/outgoing/$type',
            '/api/modelCatalogue/core/user/$id/incoming/$type',
        ]
    }

    @Unroll
    def "UserUrlMapping DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
            '/api/modelCatalogue/core/user/$id',
            '/api/modelCatalogue/core/user/$id/outgoing/$type',
            '/api/modelCatalogue/core/user/$id/incoming/$type',
            '/api/modelCatalogue/core/user/$id/favourite',
        ]
    }
}
