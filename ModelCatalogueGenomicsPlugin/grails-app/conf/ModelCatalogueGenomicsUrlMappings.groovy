import org.springframework.http.HttpMethod

class ModelCatalogueGenomicsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsCsv/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsCsv', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsJson/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsJson', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/gelSpecificationDoc" (controller: 'genomics', action: 'exportGelSpecification', method: HttpMethod.GET)
    }

}
