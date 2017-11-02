import org.springframework.http.HttpMethod

class ReferenceTypeUrlMappings {

    static mappings = {

// ReferenceType
        "/api/modelCatalogue/core/referenceType"(controller: 'referenceType', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType"(controller: 'referenceType', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/search/$search?"(controller: 'referenceType', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/validate"(controller: 'referenceType', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/validate"(controller: 'referenceType', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/$id"(controller: 'referenceType', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id"(controller: 'referenceType', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/referenceType/$id"(controller: 'referenceType', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/referenceType/$id/outgoing/search"(controller: 'referenceType', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/outgoing/$type/search"(controller: 'referenceType', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/outgoing/$type"(controller: 'referenceType', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/outgoing/$type"(controller: 'referenceType', action: 'addOutgoing', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/$id/outgoing/$type"(controller: 'referenceType', action: 'removeOutgoing', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/referenceType/$id/outgoing/$type"(controller: 'referenceType', action: 'reorderOutgoing', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/referenceType/$id/incoming/search"(controller: 'referenceType', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/incoming/$type/search"(controller: 'referenceType', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/incoming/$type"(controller: 'referenceType', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/incoming/$type"(controller: 'referenceType', action: 'addIncoming', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/$id/incoming/$type"(controller: 'referenceType', action: 'removeIncoming', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/referenceType/$id/incoming/$type"(controller: 'referenceType', action: 'reorderIncoming', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/referenceType/$id/incoming"(controller: 'referenceType', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/outgoing"(controller: 'referenceType', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/mapping/$destination"(controller: 'referenceType', action: 'addMapping', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/$id/mapping/$destination"(controller: 'referenceType', action: 'removeMapping', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/referenceType/$id/mapping"(controller: 'referenceType', action: 'mappings', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/typeHierarchy"(controller: 'referenceType', action: 'typeHierarchy', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/history"(controller: 'referenceType', action: 'history', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/path"(controller: 'referenceType', action: 'path', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/archive"(controller: 'referenceType', action: 'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/$id/restore"(controller: 'referenceType', action: 'restore', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/$id/clone/$destinationDataModelId"(controller: 'referenceType', action: 'cloneElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/$source/merge/$destination"(controller: 'referenceType', action: 'merge', method: HttpMethod.POST)
        "/api/modelCatalogue/core/referenceType/$id/dataElement"(controller: 'referenceType', action: 'dataElements', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/convert/$destination"(controller: 'referenceType', action: 'convert', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/validateValue"(controller: 'referenceType', action: 'validateValue', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/content"(controller: 'referenceType', action: 'content', method: HttpMethod.GET)
    }
}
