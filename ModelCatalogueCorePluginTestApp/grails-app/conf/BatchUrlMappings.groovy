import org.springframework.http.HttpMethod

class BatchUrlMappings {

    static mappings = {
        // Batch
        "/api/modelCatalogue/core/batch" (controller: 'batch', action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/batch" (controller: 'batch', action: 'save', method: HttpMethod.POST)
        "/api/modelCatalogue/core/batch/search/$search?" (controller: 'batch', action: 'search', method: HttpMethod.GET)
        "/api/modelCatalogue/core/batch/$id/validate" (controller: 'batch', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/batch/validate" (controller: 'batch', action: 'validate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/batch/$id" (controller: 'batch', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/batch/$id" (controller: 'batch', action: 'update', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/batch/$id" (controller: 'batch', action: 'delete', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/batch/$id/archive"(controller: 'batch', action:  'archive', method: HttpMethod.POST)
        "/api/modelCatalogue/core/batch/$id/run"(controller: 'batch', action: 'runAll', method: HttpMethod.POST)
        "/api/modelCatalogue/core/batch/$id/actions/$state?"(controller: 'batch', action: 'listActions', method: HttpMethod.GET)
        "/api/modelCatalogue/core/batch/$id/actions/$actionId/dismiss"(controller: 'batch', action: 'dismiss', method: HttpMethod.POST)
        "/api/modelCatalogue/core/batch/$id/actions/$actionId/reactivate"(controller: 'batch', action: 'reactivate', method: HttpMethod.POST)
        "/api/modelCatalogue/core/batch/$id/actions/$actionId/run"(controller: 'batch', action: 'run', method: HttpMethod.POST)
        "/api/modelCatalogue/core/batch/$id/actions/$actionId/parameters"(controller: 'batch', action: 'updateActionParameters', method: HttpMethod.PUT)
        "/api/modelCatalogue/core/batch/$id/actions/$actionId/dependsOn"(controller: 'batch', action: 'removeDependency', method: HttpMethod.DELETE)
        "/api/modelCatalogue/core/batch/$id/actions/$actionId/dependsOn"(controller: 'batch', action: 'addDependency', method: HttpMethod.POST)
    }
}

