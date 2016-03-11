import org.springframework.http.HttpMethod

class ModelCatalogueGenomicsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/genomics/gelSpecificationDoc"(controller: 'genomics', action: 'exportGelSpecification', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseases/$id" (controller: 'genomics', action: 'exportRareDiseases', method: HttpMethod.GET)
        "/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTests/$id" (controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTests', method: HttpMethod.GET)
    }

}
