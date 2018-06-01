package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.IgnoreIf
import spock.lang.Ignore
import spock.lang.Unroll

@Ignore
@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class ModelCatalogueVersionUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "ModelCatalogueVersionUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << ['/modelCatalogueVersion/index',]
    }
}
