import org.springframework.http.HttpMethod

class DataTypeUrlMappings {

    static mappings = {
        // DataType
        "/api/modelCatalogue/core/dataType"(controller: 'dataType', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType"(controller: 'dataType', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/search/$search?"(controller: 'dataType', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/validate"(controller: 'dataType', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/validate"(controller: 'dataType', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/$id"(controller: 'dataType', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id"(controller: 'dataType', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/dataType/$id"(controller: 'dataType', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/dataType/$id/outgoing/search"(controller: 'dataType', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/outgoing/$type/search"(controller: 'dataType', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/outgoing/$type"(controller: 'dataType', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/outgoing/$type"(controller: 'dataType', action: 'addOutgoing', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/$id/outgoing/$type"(controller: 'dataType', action: 'removeOutgoing', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/dataType/$id/outgoing/$type"(controller: 'dataType', action: 'reorderOutgoing', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/dataType/$id/incoming/search"(controller: 'dataType', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/incoming/$type/search"(controller: 'dataType', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/incoming/$type"(controller: 'dataType', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/incoming/$type"(controller: 'dataType', action: 'addIncoming', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/$id/incoming/$type"(controller: 'dataType', action: 'removeIncoming', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/dataType/$id/incoming/$type"(controller: 'dataType', action: 'reorderIncoming', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/dataType/$id/incoming"(controller: 'dataType', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/outgoing"(controller: 'dataType', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/mapping/$destination"(controller: 'dataType', action: 'addMapping', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/$id/mapping/$destination"(controller: 'dataType', action: 'removeMapping', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/dataType/$id/mapping"(controller: 'dataType', action: 'mappings', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/typeHierarchy"(controller: 'dataType', action: 'typeHierarchy', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/history"(controller: 'dataType', action: 'history', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/path"(controller: 'dataType', action: 'path', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/archive"(controller: 'dataType', action: 'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/$id/restore"(controller: 'dataType', action: 'restore', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/$id/clone/$destinationDataModelId"(controller: 'dataType', action: 'cloneElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/$source/merge/$destination"(controller: 'dataType', action: 'merge', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataType/$id/dataElement"(controller: 'dataType', action: 'dataElements', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/convert/$destination"(controller: 'dataType', action: 'convert', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/validateValue"(controller: 'dataType', action: 'validateValue', method: HttpMethod.GET)
    }
}