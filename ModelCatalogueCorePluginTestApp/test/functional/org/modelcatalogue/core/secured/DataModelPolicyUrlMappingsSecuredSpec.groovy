package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Unroll
import spock.lang.Ignore
import spock.lang.IgnoreIf

@Ignore
//@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class DataModelPolicyUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "DataModelPolicyUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/dataModelPolicy',
                '/api/modelCatalogue/core/dataModelPolicy/$id/validate',
                '/api/modelCatalogue/core/dataModelPolicy/validate',
        ]
    }

    @Unroll
    def "DataModelPolicyUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
                '/api/modelCatalogue/core/dataModelPolicy',
                '/api/modelCatalogue/core/dataModelPolicy/search/$search',
                '/api/modelCatalogue/core/dataModelPolicy/$id',
        ]
    }

    @Unroll
    def "DataModelPolicyUrlMappings PUT #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.put("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/dataModelPolicy/$id',
        ]
    }

    @Unroll
    def "DataModelPolicyUrlMappings DELETE #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.delete("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/dataModelPolicy/$id',
        ]
    }
}
