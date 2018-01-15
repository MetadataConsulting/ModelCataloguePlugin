import org.springframework.http.HttpMethod

class ModelCatalogueNorthThamesUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/northThames/northThamesGridHierarchyMappingSummaryReport/$id" (controller: 'northThames', action: 'northThamesGridHierarchyMappingSummaryReport', method: HttpMethod.GET)
        "/api/modelCatalogue/core/northThames/northThamesMappingReport/$id" (controller: 'northThames', action: 'northThamesMappingReport', method: HttpMethod.GET)
    }

}
