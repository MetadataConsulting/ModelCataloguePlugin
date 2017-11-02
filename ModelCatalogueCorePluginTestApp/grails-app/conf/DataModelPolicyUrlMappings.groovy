import org.springframework.http.HttpMethod

class DataModelPolicyUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/dataModelPolicy"(controller: 'dataModelPolicy', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataModelPolicy"(controller: 'dataModelPolicy', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataModelPolicy/search/$search?"(controller: 'dataModelPolicy', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataModelPolicy/$id/validate"(controller: 'dataModelPolicy', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataModelPolicy/validate"(controller: 'dataModelPolicy', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataModelPolicy/$id"(controller: 'dataModelPolicy', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataModelPolicy/$id"(controller: 'dataModelPolicy', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/dataModelPolicy/$id"(controller: 'dataModelPolicy', action: 'delete', method: HttpMethod.DELETE)
    }
}
