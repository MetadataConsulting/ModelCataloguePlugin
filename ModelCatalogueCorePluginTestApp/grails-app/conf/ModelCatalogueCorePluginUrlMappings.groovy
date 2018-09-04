import org.springframework.http.HttpMethod

class ModelCatalogueCorePluginUrlMappings {

	static mappings = {

        "/api/modelCatalogue/core/forms/generate/$id" (controller: 'formGenerator', action: 'generateForm', method: HttpMethod.GET)
        "/api/modelCatalogue/core/forms/preview/$id" (controller: 'formGenerator', action: 'previewForm', method: HttpMethod.GET)

        "/catalogue/upload" (controller: "dataImport", action: 'upload', method: HttpMethod.POST)

        // Catalogue
        "/catalogue/ext/$key/$value" (controller: 'catalogue', action: 'ext', method: HttpMethod.GET)
        "/catalogue/ext/$key/$value/export" (controller: 'catalogue', action: 'ext', method: HttpMethod.GET)
        "/catalogue/$resource/$id(.${version})?" (controller: 'catalogue', action: 'xref', method: HttpMethod.GET)
        "/catalogue/$resource/$id(.${version})?/export" (controller: 'catalogue', action: 'xref', method: HttpMethod.GET) {
            format = 'xml'
        }

        "/api/modelCatalogue/core/feedback"(controller: 'catalogue', action: 'feedbacks', method: HttpMethod.GET)
        "/api/modelCatalogue/core/feedback/$key"(controller: 'catalogue', action: 'feedback', method: HttpMethod.GET)
        "/api/modelCatalogue/core/logs"(controller: 'logging', action: 'logsToAssets', method: HttpMethod.GET)

        "/"(view:"index")
        "/load"(view:"load")
        "/api/modelCatalogue/core/search/reindex" (controller:"search", action : 'reindex', method: HttpMethod.POST)
        "/api/modelCatalogue/core/search/$search?" (controller:"search", action : 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/relationship/$id/restore" (controller:"relationship", action : 'restore', method: HttpMethod.POST)

        "/api/modelCatalogue/register"(controller: "apiRegister", action: "register")
	}
}
