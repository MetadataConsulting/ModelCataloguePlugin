package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.IgnoreIf
import spock.lang.Unroll
import spock.lang.Ignore

@Ignore
//@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class ReindexCatalogueUrlMappingsSecuredSpec extends GebSpec {

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
        endpoint << ["/reindexCatalogue/index",]
    }
}
