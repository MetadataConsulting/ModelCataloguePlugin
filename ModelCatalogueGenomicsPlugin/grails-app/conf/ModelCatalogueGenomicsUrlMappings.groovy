import org.springframework.http.HttpMethod

class ModelCatalogueGenomicsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/genomics/exportGelSpecification/$id" (controller: 'genomics', action: 'exportGelSpecification', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsJson/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsJson', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsCsv/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsCsv', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTests/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTests', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityDoc/$id"(controller: 'genomics', action: 'exportRareDiseaseEligibilityDoc', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseasePhenotypesAndClinicalTestsDoc/$id"(controller: 'genomics', action: 'exportRareDiseasePhenotypesAndClinicalTestsDoc', method: HttpMethod.GET)
    }

}
