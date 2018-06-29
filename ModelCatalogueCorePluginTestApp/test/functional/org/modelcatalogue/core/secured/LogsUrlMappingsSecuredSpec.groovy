package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.IgnoreIf
import spock.lang.Ignore
import spock.lang.Unroll

@Ignore
//@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class LogsUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "LogsUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << ["/logs/index",]
    }
}
