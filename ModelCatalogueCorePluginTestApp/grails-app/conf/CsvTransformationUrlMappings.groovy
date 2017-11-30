import org.springframework.http.HttpMethod

class CsvTransformationUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/csvTransformation"(controller: 'csvTransformation', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/csvTransformation"(controller: 'csvTransformation', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/csvTransformation/search/$search?"(controller: 'csvTransformation', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/csvTransformation/$id/validate"(controller: 'csvTransformation', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/csvTransformation/validate"(controller: 'csvTransformation', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/csvTransformation/$id"(controller: 'csvTransformation', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/csvTransformation/$id"(controller: 'csvTransformation', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/csvTransformation/$id"(controller: 'csvTransformation', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/csvTransformation/$id/transform"(controller: 'csvTransformation', action: 'transform', method: HttpMethod.POST)
    }
}
