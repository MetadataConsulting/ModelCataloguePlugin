import org.springframework.http.HttpMethod

class ModelCatalogueGenomicsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/genomics/gelSpecificationDoc"(controller: 'genomics', action: 'exportGelSpecification', method: HttpMethod.GET)
    }

}
