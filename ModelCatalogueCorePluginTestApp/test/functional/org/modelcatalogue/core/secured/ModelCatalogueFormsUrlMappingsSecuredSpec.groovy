package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import spock.lang.Unroll
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class ModelCatalogueFormsUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "ModelCatalogueFormsUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
                '/api/modelCatalogue/core/forms/generate/$id',
                '/api/modelCatalogue/core/forms/preview/$id',
        ]
    }
}