import org.springframework.http.HttpMethod

class ModelCatalogueCorePluginUrlMappings {


	static mappings = {

        "/api/modelCatalogue/core/change/" (controller: 'change', action: 'global', method: HttpMethod.GET)
        "/api/modelCatalogue/core/change/$id" (controller: 'change', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/change/$id/changes" (controller: 'change', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/change/$id" (controller: 'change', action: 'undo', method: HttpMethod.DELETE)

        "/api/modelCatalogue/core/$controllerName/$id/changes"(controller: controllerName, action: 'changes', method: HttpMethod.GET)

        "/api/modelCatalogue/core/classification/$id/activity"(controller: 'change', action: 'classificationActivity', method: HttpMethod.GET)
        "/api/modelCatalogue/core/user/$id/activity"(controller: 'change', action: 'userActivity', method: HttpMethod.GET)

	}
}
