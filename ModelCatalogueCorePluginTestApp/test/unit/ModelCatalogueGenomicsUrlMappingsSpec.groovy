import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.genomics.GenomicsController
import org.modelcatalogue.core.genomics.RareDiseaseImportController
import spock.lang.Specification

@TestFor(ModelCatalogueGenomicsUrlMappings)
@Mock([GenomicsController, RareDiseaseImportController])
class ModelCatalogueGenomicsUrlMappingsSpec extends Specification {

    void "test ModelCatalogueGenomicsUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/imports/upload', controller: "rareDiseaseImport", action: 'upload')
    }

    void "test ModelCatalogueGenomicsUrlMappings GET Request mappings"() {
        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsJson/$id', controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsJson')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseListAsJson/$id', controller: 'genomics', action: 'exportRareDiseaseListAsJson')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseHPOEligibilityCriteriaAsJson/$id', controller: 'genomics', action: 'exportRareDiseaseHPOEligibilityCriteriaAsJson')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsCsv/$id', controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsCsv')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsXls/$id', controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsXls')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityDoc/$id', controller: 'genomics', action: 'exportRareDiseaseEligibilityDoc')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseasePhenotypesAndClinicalTestsDoc/$id', controller: 'genomics', action: 'exportRareDiseasePhenotypesAndClinicalTestsDoc')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseDisorderListCsv/$id', controller: 'genomics', action: 'exportRareDiseaseDisorderListAsCsv')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityCsv/$id', controller: 'genomics', action: 'exportRareDiseaseEligibilityCsv')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportCancerTypesAsJson/$id', controller: 'genomics', action: 'exportCancerTypesAsJson')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportCancerTypesAsCsv/$id', controller: 'genomics', action: 'exportCancerTypesAsCsv')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportChangeLogDocument/$id', controller: 'genomics', action: 'exportChangeLogDocument')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityChangeLogAsXls/$id', controller: 'genomics', action: 'exportRareDiseaseEligibilityChangeLogAsXls')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportDataSpecChangeLogAsXls/$id', controller: 'genomics', action: 'exportDataSpecChangeLogAsXls')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportAllCancerReports/$id', controller: 'genomics', action: 'exportAllCancerReports')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportAllRareDiseaseReports/$id', controller: 'genomics', action: 'exportAllRareDiseaseReports')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseasesWebsite/$id', controller: 'genomics', action: 'exportRareDiseasesWebsite')
        assertForwardUrlMapping('/api/modelCatalogue/core/genomics/exportRareDiseaseSplitDocs/$id', controller: 'genomics', action: 'exportRareDiseaseSplitDocs')
    }
}
