import org.springframework.http.HttpMethod

class TagUrlMappings {

    static mappings = {
        // Tag
        "/api/modelCatalogue/core/tag/forDataModel/$dataModelId"(controller: 'tag', action: 'forDataModel', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag" (controller: 'tag', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag" (controller: 'tag', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/search/$search?" (controller: 'tag', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/validate" (controller: 'tag', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/validate" (controller: 'tag', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/$id" (controller: 'tag', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id" (controller: 'tag', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/tag/$id" (controller: 'tag', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/tag/$id/outgoing/search" (controller: 'tag', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/outgoing/$type/search" (controller: 'tag', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/outgoing/$type" (controller: 'tag', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/outgoing/$type" (controller: 'tag', action: 'addOutgoing', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/$id/outgoing/$type" (controller: 'tag', action: 'removeOutgoing', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/tag/$id/outgoing/$type" (controller: 'tag', action: 'reorderOutgoing', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/tag/$id/incoming/search" (controller: 'tag', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/incoming/$type/search" (controller: 'tag', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/incoming/$type" (controller: 'tag', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/incoming/$type" (controller: 'tag', action: 'addIncoming', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/$id/incoming/$type" (controller: 'tag', action: 'removeIncoming', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/tag/$id/incoming/$type" (controller: 'tag', action: 'reorderIncoming', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/tag/$id/incoming" (controller: 'tag', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/outgoing" (controller: 'tag', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/mapping/$destination" (controller: 'tag', action: 'addMapping', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/$id/mapping/$destination" (controller: 'tag', action: 'removeMapping', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/tag/$id/mapping" (controller: 'tag', action: 'mappings', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/typeHierarchy" (controller: 'tag', action: 'typeHierarchy', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/history"(controller: 'tag', action: 'history', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/path"(controller: 'tag', action: 'path', method: HttpMethod.GET)
        "/api/modelCatalogue/core/tag/$id/archive"(controller: 'tag', action: 'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/$id/restore"(controller: 'tag', action: 'restore', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/$id/finalize"(controller: 'tag', action: 'finalizeElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/$id/clone/$destinationDataModelId"(controller: 'tag', action: 'cloneElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/tag/$source/merge/$destination"(controller: 'tag', action: 'merge', method: HttpMethod.POST)

    }
}