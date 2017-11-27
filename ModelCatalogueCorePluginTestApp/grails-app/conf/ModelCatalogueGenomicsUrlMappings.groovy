import org.springframework.http.HttpMethod

class ModelCatalogueGenomicsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/genomics/imports/upload" (controller: "rareDiseaseImport", action: 'upload', method: HttpMethod.POST)

        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsJson/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsJson', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseListAsJson/$id" (controller: 'genomics', action: 'exportRareDiseaseListAsJson', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOEligibilityCriteriaAsJson/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOEligibilityCriteriaAsJson', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsCsv/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsCsv', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsXls/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsXls', method: HttpMethod.GET)
//        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTests/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTests', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityDoc/$id"(controller: 'genomics', action: 'exportRareDiseaseEligibilityDoc', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseasePhenotypesAndClinicalTestsDoc/$id"(controller: 'genomics', action: 'exportRareDiseasePhenotypesAndClinicalTestsDoc', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseDisorderListCsv/$id"(controller: 'genomics', action: 'exportRareDiseaseDisorderListAsCsv', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityCsv/$id"(controller: 'genomics', action: 'exportRareDiseaseEligibilityCsv', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportCancerTypesAsJson/$id"(controller: 'genomics', action: 'exportCancerTypesAsJson', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportCancerTypesAsCsv/$id"(controller: 'genomics', action: 'exportCancerTypesAsCsv', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportChangeLogDocument/$id"(controller: 'genomics', action: 'exportChangeLogDocument', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityChangeLogAsXls/$id" (controller: 'genomics', action: 'exportRareDiseaseEligibilityChangeLogAsXls', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportDataSpecChangeLogAsXls/$id" (controller: 'genomics', action: 'exportDataSpecChangeLogAsXls', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportAllCancerReports/$id" (controller: 'genomics', action: 'exportAllCancerReports', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportAllRareDiseaseReports/$id" (controller: 'genomics', action: 'exportAllRareDiseaseReports', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseasesWebsite/$id" (controller: 'genomics', action: 'exportRareDiseasesWebsite', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseSplitDocs/$id" (controller: 'genomics', action: 'exportRareDiseaseSplitDocs', method: HttpMethod.GET)

    }

}
