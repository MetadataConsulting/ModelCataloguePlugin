import org.springframework.http.HttpMethod

class ValidationRuleUrlMappings {

    static mappings = {
        // ValidationRule
        "/api/modelCatalogue/core/validationRule"(controller: 'validationRule', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule"(controller: 'validationRule', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/search/$search?"(controller: 'validationRule', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/validate"(controller: 'validationRule', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/validate"(controller: 'validationRule', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$id"(controller: 'validationRule', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id"(controller: 'validationRule', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/validationRule/$id"(controller: 'validationRule', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/validationRule/$id/outgoing/search"(controller: 'validationRule', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/outgoing/$type/search"(controller: 'validationRule', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/outgoing/$type"(controller: 'validationRule', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/outgoing/$type"(controller: 'validationRule', action: 'addOutgoing', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$id/outgoing/$type"(controller: 'validationRule', action: 'removeOutgoing', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/validationRule/$id/outgoing/$type"(controller: 'validationRule', action: 'reorderOutgoing', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/validationRule/$id/incoming/search"(controller: 'validationRule', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/incoming/$type/search"(controller: 'validationRule', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/incoming/$type"(controller: 'validationRule', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/incoming/$type"(controller: 'validationRule', action: 'addIncoming', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$id/incoming/$type"(controller: 'validationRule', action: 'removeIncoming', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/validationRule/$id/incoming/$type"(controller: 'validationRule', action: 'reorderIncoming', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/validationRule/$id/incoming"(controller: 'validationRule', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/outgoing"(controller: 'validationRule', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/mapping/$destination"(controller: 'validationRule', action: 'addMapping', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$id/mapping/$destination"(controller: 'validationRule', action: 'removeMapping', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/validationRule/$id/mapping"(controller: 'validationRule', action: 'mappings', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/typeHierarchy"(controller: 'validationRule', action: 'typeHierarchy', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/history"(controller: 'validationRule', action: 'history', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/path"(controller: 'validationRule', action: 'path', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/archive"(controller: 'validationRule', action: 'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$id/restore"(controller: 'validationRule', action: 'restore', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$id/finalize"(controller: 'validationRule', action: 'finalizeElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$id/clone/$destinationDataModelId"(controller: 'validationRule', action: 'cloneElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$source/merge/$destination"(controller: 'validationRule', action: 'merge', method: HttpMethod.POST)
        "/api/modelCatalogue/core/validationRule/$id/content"(controller: 'validationRule', action: 'content', method: HttpMethod.GET)
    }
}