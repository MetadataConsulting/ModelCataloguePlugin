import org.springframework.http.HttpMethod

class ModelCatalogueNorthThamesUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/northThames/northThamesSummaryReport/$id" (controller: 'northThames', action: 'northThamesSummaryReport', method: HttpMethod.GET)
    }

}
