package org.modelcatalogue.core.secured

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class DataModelUrlMappingsSecuredSpec extends Specification {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "DataModelUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataModel',
        '/api/modelCatalogue/core/dataModel/$id/validate',
        '/api/modelCatalogue/core/dataModel/validate',
        '/api/modelCatalogue/core/dataModel/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataModel/$id/reindex',
        '/api/modelCatalogue/core/dataModel/$id/incoming/$type',
        '/api/modelCatalogue/core/dataModel/$id/newVersion',
        '/api/modelCatalogue/core/dataModel/$id/mapping/$destination',
        '/api/modelCatalogue/core/dataModel/$id/archive',
        '/api/modelCatalogue/core/dataModel/$id/restore',
        '/api/modelCatalogue/core/dataModel/$id/finalize',
        '/api/modelCatalogue/core/dataModel/$id/clone/$destinationDataModelId',
        '/api/modelCatalogue/core/dataModel/$source/merge/$destination',
        '/api/modelCatalogue/core/dataModel/preload',
                ]
    }

    @Unroll
    def "DataModelUrlMappings GET #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.get("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataModel',
        '/api/modelCatalogue/core/dataModel/search/$search',
        '/api/modelCatalogue/core/dataModel/$id',
        '/api/modelCatalogue/core/dataModel/$id/outgoing/search',
        '/api/modelCatalogue/core/dataModel/$id/outgoing/$type/search',
        '/api/modelCatalogue/core/dataModel/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataModel/$id/incoming/search',
        '/api/modelCatalogue/core/dataModel/$id/incoming/$type/search',
        '/api/modelCatalogue/core/dataModel/$id/incoming/$type',
        '/api/modelCatalogue/core/dataModel/$id/incoming',
        '/api/modelCatalogue/core/dataModel/$id/outgoing',
        '/api/modelCatalogue/core/dataModel/$id/mapping',
        '/api/modelCatalogue/core/dataModel/$id/typeHierarchy',
        '/api/modelCatalogue/core/dataModel/$id/history',
        '/api/modelCatalogue/core/dataModel/$id/path',
        '/api/modelCatalogue/core/dataModel/preload',
        '/api/modelCatalogue/core/dataModel/$id/containsOrImports/$other',
        '/api/modelCatalogue/core/dataModel/$id/content',
        '/api/modelCatalogue/core/dataModel/$id/inventorySpreadsheet',
        '/api/modelCatalogue/core/dataModel/$id/gridSpreadsheet',
        '/api/modelCatalogue/core/dataModel/$id/excelExporterSpreadsheet',
        '/api/modelCatalogue/core/dataModel/$id/inventoryDoc',
        '/api/modelCatalogue/core/dataModel/$id/dependents',
                ]
    }

    @Unroll
    def "DataModelUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataModel/$id',
        '/api/modelCatalogue/core/dataModel/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataModel/$id/incoming/$type',
                ]
    }

    @Unroll
    def "DataModelUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 401

        where:
        endpoint << [
        '/api/modelCatalogue/core/dataModel/$id',
        '/api/modelCatalogue/core/dataModel/$id/outgoing/$type',
        '/api/modelCatalogue/core/dataModel/$id/incoming/$type',
        '/api/modelCatalogue/core/dataModel/$id/mapping/$destination',
                ]
    }
}
