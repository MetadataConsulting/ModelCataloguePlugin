import org.springframework.http.HttpMethod

class RelationshipTypeUrlMappings {

    static mappings = {
        // RelationshipType
        "/api/modelCatalogue/core/relationshipType"(controller: 'relationshipType', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/relationshipType"(controller: 'relationshipType', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/relationshipType/search/$search?"(controller: 'relationshipType', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/relationshipType/$id/validate"(controller: 'relationshipType', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/relationshipType/validate"(controller: 'relationshipType', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/relationshipType/$id"(controller: 'relationshipType', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/relationshipType/$id"(controller: 'relationshipType', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/relationshipType/$id"(controller: 'relationshipType', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/relationshipType/elementClasses"(controller: 'relationshipType', action: 'elementClasses', method: HttpMethod.GET)
    }
}