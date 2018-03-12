package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.modelcatalogue.core.geb.LoginPage
import org.springframework.http.HttpMethod
import spock.lang.IgnoreIf
import spock.lang.Unroll

@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class DataImportCreateUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "ApiKeyUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << ["/dataImport/obo",
                     "/dataImport/dsl",
                     "/dataImport/excel",
                     "/dataImport/xml",]
    }
}
