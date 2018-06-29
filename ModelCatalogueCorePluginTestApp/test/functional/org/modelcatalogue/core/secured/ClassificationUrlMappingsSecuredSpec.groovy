package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Unroll
import spock.lang.Ignore
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('spock.ignore.secured') })
@Ignore
class ClassificationUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "ClassificationUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/classification',
                '/api/modelCatalogue/core/classification/$id/validate',
                '/api/modelCatalogue/core/classification/validate',
                '/api/modelCatalogue/core/classification/$id/outgoing/$type',
                '/api/modelCatalogue/core/classification/$id/incoming/$type',
                '/api/modelCatalogue/core/classification/$id/mapping/$destination',
                '/api/modelCatalogue/core/classification/$id/archive',
                '/api/modelCatalogue/core/classification/$id/restore',
                '/api/modelCatalogue/core/classification/$id/clone/$destinationDataModelId',
                '/api/modelCatalogue/core/classification/$source/merge/$destination',
                '/api/modelCatalogue/core/classification/preload',
                '/api/modelCatalogue/core/classification/$id/newVersion',
                '/api/modelCatalogue/core/classification/$id/reindex',
        ]
    }

    @Unroll
    def "ClassificationUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
                '/api/modelCatalogue/core/classification/$id/incoming',
                '/api/modelCatalogue/core/classification/$id/outgoing',
                '/api/modelCatalogue/core/classification/preload',
                '/api/modelCatalogue/core/classification/$id/inventorySpreadsheet',
                '/api/modelCatalogue/core/classification/$id/incoming/search',
                '/api/modelCatalogue/core/classification/$id/incoming/$type/search',
                '/api/modelCatalogue/core/classification/$id/incoming/$type',
                '/api/modelCatalogue/core/classification',
                '/api/modelCatalogue/core/classification/search/$search',
                '/api/modelCatalogue/core/classification/$id',
                '/api/modelCatalogue/core/classification/$id/outgoing/search',
                '/api/modelCatalogue/core/classification/$id/outgoing/$type/search',
                '/api/modelCatalogue/core/classification/$id/outgoing/$type',
                '/api/modelCatalogue/core/classification/$id/gridSpreadsheet',
                '/api/modelCatalogue/core/classification/$id/excelExporterSpreadsheet',
                '/api/modelCatalogue/core/classification/$id/inventoryDoc',
                '/api/modelCatalogue/core/classification/$id/dependents',
                '/api/modelCatalogue/core/classification/$id/containsOrImports/$other',
                '/api/modelCatalogue/core/classification/$id/content',
                '/api/modelCatalogue/core/classification/$id/mapping',
                '/api/modelCatalogue/core/classification/$id/typeHierarchy',
                '/api/modelCatalogue/core/classification/$id/history',
                '/api/modelCatalogue/core/classification/$id/path',
        ]
    }

    @Unroll
    def "ClassificationUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/classification/$id',
                '/api/modelCatalogue/core/classification/$id/outgoing/$type',
                '/api/modelCatalogue/core/classification/$id/incoming/$type',
        ]
    }

    @Unroll
    def "ClassificationUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/classification/$id',
                '/api/modelCatalogue/core/classification/$id/outgoing/$type',
                '/api/modelCatalogue/core/classification/$id/incoming/$type',
                '/api/modelCatalogue/core/classification/$id/mapping/$destination',
        ]
    }
}
