package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Unroll
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class DataClassUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "DataClassUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataClass',
        '/api/modelCatalogue/core/dataClass/$id/validate',
        '/api/modelCatalogue/core/dataClass/validate',
        '/api/modelCatalogue/core/dataClass/$id/outgoing/$type',
        '/api/modelCatalogue/core/model/$id/archive',
        '/api/modelCatalogue/core/model/$id/restore',
        '/api/modelCatalogue/core/model/$id/clone/$destinationDataModelId',
        '/api/modelCatalogue/core/model/$source/merge/$destination',
        '/api/modelCatalogue/core/model/$id/mapping/$destination',
        '/api/modelCatalogue/core/dataClass/$id/mapping/$destination',
        '/api/modelCatalogue/core/model/$id/incoming/$type',
        '/api/modelCatalogue/core/model/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataClass/$id/incoming/$type',
        '/api/modelCatalogue/core/model/$id/validate',
        '/api/modelCatalogue/core/model/validate',
        '/api/modelCatalogue/core/model',
        '/api/modelCatalogue/core/dataClass/$id/archive',
        '/api/modelCatalogue/core/dataClass/$id/restore',
        '/api/modelCatalogue/core/dataClass/$id/clone/$destinationDataModelId',
        '/api/modelCatalogue/core/dataClass/$source/merge/$destination',
                ]
    }

    @Unroll
    def "DataClassUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataClass',
        '/api/modelCatalogue/core/dataClass/search/$search',
        '/api/modelCatalogue/core/dataClass/$id',
        '/api/modelCatalogue/core/dataClass/$id/outgoing/search',
        '/api/modelCatalogue/core/dataClass/$id/outgoing/$type/search',
        '/api/modelCatalogue/core/dataClass/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataClass/$id/incoming/search',
        '/api/modelCatalogue/core/dataClass/$id/incoming/$type/search',
        '/api/modelCatalogue/core/dataClass/$id/incoming/$type',
        '/api/modelCatalogue/core/dataClass/$id/incoming',
        '/api/modelCatalogue/core/dataClass/$id/outgoing',
        '/api/modelCatalogue/core/dataClass/$id/mapping',
        '/api/modelCatalogue/core/dataClass/$id/typeHierarchy',
        '/api/modelCatalogue/core/dataClass/$id/history',
        '/api/modelCatalogue/core/dataClass/$id/path',
        '/api/modelCatalogue/core/dataClass/$id/inventoryDoc',
        '/api/modelCatalogue/core/dataClass/$id/classificationChangelog',
        '/api/modelCatalogue/core/dataClass/$id/inventorySpreadsheet',
        '/api/modelCatalogue/core/dataClass/$id/referenceType',
        '/api/modelCatalogue/core/dataClass/$id/content',
        '/api/modelCatalogue/core/model',
        '/api/modelCatalogue/core/model/search/$search',
        '/api/modelCatalogue/core/model/$id',
        '/api/modelCatalogue/core/model/$id/outgoing/search',
        '/api/modelCatalogue/core/model/$id/outgoing/$type/search',
        '/api/modelCatalogue/core/model/$id/outgoing/$type',
        '/api/modelCatalogue/core/model/$id/incoming/search',
        '/api/modelCatalogue/core/model/$id/incoming/$type/search',
        '/api/modelCatalogue/core/model/$id/incoming/$type',
        '/api/modelCatalogue/core/model/$id/incoming',
        '/api/modelCatalogue/core/model/$id/outgoing',
        '/api/modelCatalogue/core/model/$id/mapping',
        '/api/modelCatalogue/core/model/$id/typeHierarchy',
        '/api/modelCatalogue/core/model/$id/history',
        '/api/modelCatalogue/core/model/$id/path',
        '/api/modelCatalogue/core/model/$id/inventoryDoc',
        '/api/modelCatalogue/core/model/$id/classificationChangelog',
        '/api/modelCatalogue/core/model/$id/inventorySpreadsheet',
        '/api/modelCatalogue/core/model/$id/referenceType',
        '/api/modelCatalogue/core/model/$id/content',
        ]
    }

    @Unroll
    def "DataClassUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataClass/$id',
        '/api/modelCatalogue/core/model/$id/incoming/$type',
        '/api/modelCatalogue/core/model/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataClass/$id/outgoing/$type',
        '/api/modelCatalogue/core/model/$id',
        '/api/modelCatalogue/core/dataClass/$id/incoming/$type',
                ]
    }

    @Unroll
    def "DataClassUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataClass/$id/mapping/$destination',
        '/api/modelCatalogue/core/dataClass/$id',
        '/api/modelCatalogue/core/model/$id/mapping/$destination',
        '/api/modelCatalogue/core/dataClass/$id/incoming/$type',
        '/api/modelCatalogue/core/model/$id/incoming/$type',
        '/api/modelCatalogue/core/model/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataClass/$id/outgoing/$type',
        '/api/modelCatalogue/core/model/$id',
                ]
    }
}


