import org.springframework.http.HttpMethod

class PrimitiveTypeUrlMappings {

    static mappings = {

// PrimitiveType
        "/api/modelCatalogue/core/primitiveType"(controller: 'primitiveType', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType"(controller: 'primitiveType', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/search/$search?"(controller: 'primitiveType', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/validate"(controller: 'primitiveType', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/validate"(controller: 'primitiveType', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$id"(controller: 'primitiveType', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id"(controller: 'primitiveType', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/primitiveType/$id"(controller: 'primitiveType', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/primitiveType/$id/outgoing/search"(controller: 'primitiveType', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/outgoing/$type/search"(controller: 'primitiveType', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/outgoing/$type"(controller: 'primitiveType', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/outgoing/$type"(controller: 'primitiveType', action: 'addOutgoing', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$id/outgoing/$type"(controller: 'primitiveType', action: 'removeOutgoing', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/primitiveType/$id/outgoing/$type"(controller: 'primitiveType', action: 'reorderOutgoing', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/primitiveType/$id/incoming/search"(controller: 'primitiveType', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/incoming/$type/search"(controller: 'primitiveType', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/incoming/$type"(controller: 'primitiveType', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/incoming/$type"(controller: 'primitiveType', action: 'addIncoming', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$id/incoming/$type"(controller: 'primitiveType', action: 'removeIncoming', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/primitiveType/$id/incoming/$type"(controller: 'primitiveType', action: 'reorderIncoming', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/primitiveType/$id/incoming"(controller: 'primitiveType', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/outgoing"(controller: 'primitiveType', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/mapping/$destination"(controller: 'primitiveType', action: 'addMapping', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$id/mapping/$destination"(controller: 'primitiveType', action: 'removeMapping', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/primitiveType/$id/mapping"(controller: 'primitiveType', action: 'mappings', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/typeHierarchy"(controller: 'primitiveType', action: 'typeHierarchy', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/history"(controller: 'primitiveType', action: 'history', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/path"(controller: 'primitiveType', action: 'path', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/archive"(controller: 'primitiveType', action: 'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$id/restore"(controller: 'primitiveType', action: 'restore', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$id/finalize"(controller: 'primitiveType', action: 'finalizeElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$id/clone/$destinationDataModelId"(controller: 'primitiveType', action: 'cloneElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$source/merge/$destination"(controller: 'primitiveType', action: 'merge', method: HttpMethod.POST)
        "/api/modelCatalogue/core/primitiveType/$id/dataElement"(controller: 'primitiveType', action: 'dataElements', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/convert/$destination"(controller: 'primitiveType', action: 'convert', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/validateValue"(controller: 'primitiveType', action: 'validateValue', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/content"(controller: 'primitiveType', action: 'content', method: HttpMethod.GET)
    }
}