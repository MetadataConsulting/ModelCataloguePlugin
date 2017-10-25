import org.springframework.http.HttpMethod

class DataElementUrlMappings {

    static mappings = {
        // DataElement
        "/api/modelCatalogue/core/dataElement" (controller: 'dataElement', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement" (controller: 'dataElement', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/search/$search?" (controller: 'dataElement', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/validate" (controller: 'dataElement', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/validate" (controller: 'dataElement', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$id" (controller: 'dataElement', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id" (controller: 'dataElement', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/dataElement/$id" (controller: 'dataElement', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/dataElement/$id/outgoing/search" (controller: 'dataElement', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/outgoing/$type/search" (controller: 'dataElement', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/outgoing/$type" (controller: 'dataElement', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/outgoing/$type" (controller: 'dataElement', action: 'addOutgoing', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$id/outgoing/$type" (controller: 'dataElement', action: 'removeOutgoing', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/dataElement/$id/outgoing/$type" (controller: 'dataElement', action: 'reorderOutgoing', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/dataElement/$id/incoming/search" (controller: 'dataElement', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/incoming/$type/search" (controller: 'dataElement', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/incoming/$type" (controller: 'dataElement', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/incoming/$type" (controller: 'dataElement', action: 'addIncoming', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$id/incoming/$type" (controller: 'dataElement', action: 'removeIncoming', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/dataElement/$id/incoming/$type" (controller: 'dataElement', action: 'reorderIncoming', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/dataElement/$id/incoming" (controller: 'dataElement', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/outgoing" (controller: 'dataElement', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/mapping/$destination" (controller: 'dataElement', action: 'addMapping', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$id/mapping/$destination" (controller: 'dataElement', action: 'removeMapping', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/dataElement/$id/mapping" (controller: 'dataElement', action: 'mappings', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/typeHierarchy" (controller: 'dataElement', action: 'typeHierarchy', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/history"(controller: 'dataElement', action: 'history', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/path"(controller: 'dataElement', action: 'path', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/archive"(controller: 'dataElement', action: 'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$id/restore"(controller: 'dataElement', action: 'restore', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$id/finalize"(controller: 'dataElement', action: 'finalizeElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$id/clone/$destinationDataModelId"(controller: 'dataElement', action: 'cloneElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$source/merge/$destination"(controller: 'dataElement', action: 'merge', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataElement/$id/content"(controller: 'dataElement', action: 'content', method: HttpMethod.GET)
    }
}