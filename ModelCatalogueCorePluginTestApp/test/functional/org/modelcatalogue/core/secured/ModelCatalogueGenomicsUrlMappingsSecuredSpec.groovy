package org.modelcatalogue.core.secured

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('spock.ignore.secured') })
class ModelCatalogueGenomicsUrlMappingsSecuredSpec extends GebSpec {

    protected String getBaseUrl() {
        'http://localhost:8080'
    }

    @Unroll
    def "ModelCatalogueGenomicsUrlMappings POST #endpoint is secured"(String endpoint) {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.post("${baseUrl}${endpoint}")

        then:
        noExceptionThrown()
        response.status == 302

        where:
        endpoint << [
                '/api/modelCatalogue/core/genomics/imports/upload',
        ]
    }

    @Unroll
    def "ModelCatalogueGenomicsUrlMappings GET #endpoint is secured"(String endpoint) {
        when:
        go "${baseUrl}${endpoint}"

        then:
        at LoginPage
        where:
        endpoint << [
                '/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsJson/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseListAsJson/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseHPOEligibilityCriteriaAsJson/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsCsv/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsXls/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityDoc/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseasePhenotypesAndClinicalTestsDoc/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseDisorderListCsv/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityCsv/$id',
                '/api/modelCatalogue/core/genomics/exportCancerTypesAsJson/$id',
                '/api/modelCatalogue/core/genomics/exportCancerTypesAsCsv/$id',
                '/api/modelCatalogue/core/genomics/exportChangeLogDocument/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityChangeLogAsXls/$id',
                '/api/modelCatalogue/core/genomics/exportDataSpecChangeLogAsXls/$id',
                '/api/modelCatalogue/core/genomics/exportAllCancerReports/$id',
                '/api/modelCatalogue/core/genomics/exportAllRareDiseaseReports/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseasesWebsite/$id',
                '/api/modelCatalogue/core/genomics/exportRareDiseaseSplitDocs/$id',
                ]
    }
}
