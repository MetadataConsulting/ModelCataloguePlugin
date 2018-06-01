package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore
import spock.lang.Unroll
import spock.lang.IgnoreIf

@Ignore
@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class ModelCatalogueCorePluginUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Ignore
    @Unroll
    def "ModelCatalogueCorePluginUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

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

    @Ignore
    @Unroll
    def "ModelCatalogueCorePluginUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

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
