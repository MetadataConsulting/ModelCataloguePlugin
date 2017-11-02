import org.springframework.http.HttpMethod

class CatalogueElementUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/catalogueElement" (controller: 'catalogueElement', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement" (controller: 'catalogueElement', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/search/$search?" (controller: 'catalogueElement', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/validate" (controller: 'catalogueElement', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/validate" (controller: 'catalogueElement', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/$id" (controller: 'catalogueElement', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id" (controller: 'catalogueElement', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/catalogueElement/$id" (controller: 'catalogueElement', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/catalogueElement/$id/outgoing/search" (controller: 'catalogueElement', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type/search" (controller: 'catalogueElement', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type" (controller: 'catalogueElement', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type" (controller: 'catalogueElement', action: 'addOutgoing', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type" (controller: 'catalogueElement', action: 'removeOutgoing', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type" (controller: 'catalogueElement', action: 'reorderOutgoing', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/catalogueElement/$id/incoming/search" (controller: 'catalogueElement', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/incoming/$type/search" (controller: 'catalogueElement', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/incoming/$type" (controller: 'catalogueElement', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/incoming/$type" (controller: 'catalogueElement', action: 'addIncoming', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/$id/incoming/$type" (controller: 'catalogueElement', action: 'removeIncoming', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/catalogueElement/$id/incoming/$type" (controller: 'catalogueElement', action: 'reorderIncoming', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/catalogueElement/$id/incoming" (controller: 'catalogueElement', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/outgoing" (controller: 'catalogueElement', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/mapping/$destination" (controller: 'catalogueElement', action: 'addMapping', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/$id/mapping/$destination" (controller: 'catalogueElement', action: 'removeMapping', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/catalogueElement/$id/mapping" (controller: 'catalogueElement', action: 'mappings', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/typeHierarchy" (controller: 'catalogueElement', action: 'typeHierarchy', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/history"(controller: 'catalogueElement', action: 'history', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/path"(controller: 'catalogueElement', action: 'path', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/archive"(controller: 'catalogueElement', action: 'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/$id/restore"(controller: 'catalogueElement', action: 'restore', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/$id/clone/$destinationDataModelId"(controller: 'catalogueElement', action: 'cloneElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/catalogueElement/$source/merge/$destination"(controller: 'catalogueElement', action: 'merge', method: HttpMethod.POST)
    }
}
