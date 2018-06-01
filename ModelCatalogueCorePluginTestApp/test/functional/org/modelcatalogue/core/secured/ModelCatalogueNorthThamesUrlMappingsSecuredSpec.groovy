package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Unroll
import spock.lang.Ignore
import spock.lang.IgnoreIf

@Ignore
@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class ModelCatalogueNorthThamesUrlMappingsSecuredSpec extends GebSpec {

    @Unroll
    def "ModelCatalogueNorthThamesUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage

        where:
        endpoint << [
                '/api/modelCatalogue/core/northThames/northThamesGridHierarchyMappingSummaryReport/$id',
        ]
    }
}
