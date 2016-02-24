import org.springframework.http.HttpMethod

class ModelCatalogueGenomicsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/genomics/exportRareDiseases/$id" (controller: 'genomics', action: 'exportRareDiseases', method: HttpMethod.GET)
    }

}