import org.springframework.http.HttpMethod

class AssetUrlMappings {

    static mappings = {
        // Asset
        "/api/modelCatalogue/core/asset" (controller: 'asset', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset" (controller: 'asset', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/search/$search?" (controller: 'asset', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/validate" (controller: 'asset', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/validate" (controller: 'asset', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id" (controller: 'asset', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id" (controller: 'asset', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/asset/$id" (controller: 'asset', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/asset/$id/outgoing/search" (controller: 'asset', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/outgoing/$type/search" (controller: 'asset', action: 'searchOutgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/outgoing/$type" (controller: 'asset', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/outgoing/$type" (controller: 'asset', action: 'addOutgoing', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id/outgoing/$type" (controller: 'asset', action: 'removeOutgoing', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/asset/$id/outgoing/$type" (controller: 'asset', action: 'reorderOutgoing', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/asset/$id/incoming/search" (controller: 'asset', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/incoming/$type/search" (controller: 'asset', action: 'searchIncoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/incoming/$type" (controller: 'asset', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/incoming/$type" (controller: 'asset', action: 'addIncoming', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id/incoming/$type" (controller: 'asset', action: 'removeIncoming', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/asset/$id/incoming/$type" (controller: 'asset', action: 'reorderIncoming', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/asset/$id/incoming" (controller: 'asset', action: 'incoming', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/outgoing" (controller: 'asset', action: 'outgoing', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/mapping/$destination" (controller: 'asset', action: 'addMapping', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id/mapping/$destination" (controller: 'asset', action: 'removeMapping', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/asset/$id/mapping" (controller: 'asset', action: 'mappings', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/typeHierarchy" (controller: 'asset', action: 'typeHierarchy', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/history"(controller: 'asset', action: 'history', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/path"(controller: 'asset', action: 'path', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/archive"(controller: 'asset', action: 'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id/restore"(controller: 'asset', action: 'restore', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id/finalize"(controller: 'asset', action: 'finalizeElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id/clone/$destinationDataModelId"(controller: 'asset', action: 'cloneElement', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$source/merge/$destination"(controller: 'asset', action: 'merge', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/upload"(controller: 'asset', action: 'upload', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id/upload"(controller: 'asset', action: 'upload', method: HttpMethod.POST)
        "/api/modelCatalogue/core/asset/$id/download"(controller: 'asset', action: 'download', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/content"(controller: 'asset', action: 'content', method: HttpMethod.GET)
        "/api/modelCatalogue/core/asset/$id/validateXml"(controller: 'asset', action: 'validateXml', method: HttpMethod.POST)

    }
}
