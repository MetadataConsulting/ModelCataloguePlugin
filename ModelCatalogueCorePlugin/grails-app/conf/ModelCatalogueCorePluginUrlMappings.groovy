import org.springframework.http.HttpMethod

class ModelCatalogueCorePluginUrlMappings {


	static mappings = {

        def allElements         = ['asset', 'catalogueElement', 'conceptualDomain', 'dataElement', 'dataType', 'enumeratedType', 'extendibleElement', 'measurementUnit', 'model', 'publishedElement', 'relationshipType', 'valueDomain']
        def publishedElements   = ['asset', 'dataElement', 'extendibleElement', 'model', 'publishedElement']

        for (String controllerName in allElements) {
            "/api/modelCatalogue/core/$controllerName" (controller: controllerName, action: 'index', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName" (controller: controllerName, action: 'save', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$controllerName/search/$search?" (controller: controllerName, action: 'search', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id/validate" (controller: controllerName, action: 'validate', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$controllerName/validate" (controller: controllerName, action: 'validate', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$controllerName/$id" (controller: controllerName, action: 'show', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id" (controller: controllerName, action: 'update', method: HttpMethod.PUT)
            "/api/modelCatalogue/core/$controllerName/$id" (controller: controllerName, action: 'delete', method: HttpMethod.DELETE)
            "/api/modelCatalogue/core/$controllerName/$id/relationships" (controller: controllerName, action: "relationships", method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id/relationships/$type" (controller: controllerName, action: "relationships", method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id/outgoing/$type" (controller: controllerName, action: 'outgoing', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id/outgoing/$type" (controller: controllerName, action: 'addOutgoing', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$controllerName/$id/outgoing/$type" (controller: controllerName, action: 'removeOutgoing', method: HttpMethod.DELETE)
            "/api/modelCatalogue/core/$controllerName/$id/incoming/$type" (controller: controllerName, action: 'incoming', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id/incoming/$type" (controller: controllerName, action: 'addIncoming', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$controllerName/$id/incoming/$type" (controller: controllerName, action: 'removeIncoming', method: HttpMethod.DELETE)
            "/api/modelCatalogue/core/$controllerName/$id/incoming" (controller: controllerName, action: "incoming", method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id/outgoing" (controller: controllerName, action: "outgoing", method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id/mapping/$destination" (controller: controllerName, action: 'addMapping', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$controllerName/$id/mapping/$destination" (controller: controllerName, action: 'removeMapping', method: HttpMethod.DELETE)
            "/api/modelCatalogue/core/$controllerName/$id/mapping" (controller: controllerName, action: 'mappings', method: HttpMethod.GET)

            if (controllerName in publishedElements) {
                "/api/modelCatalogue/core/$controllerName/$id/history"(controller: controllerName, action: 'history', method: HttpMethod.GET)
            }

            if (controllerName == 'dataType') {
                "/api/modelCatalogue/core/$controllerName/$id/valueDomain"(controller: controllerName, action: 'valueDomains', method: HttpMethod.GET)
            }

            if (controllerName == 'asset') {
                "/api/modelCatalogue/core/$controllerName/upload"(controller: controllerName, action: 'upload', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/upload"(controller: controllerName, action: 'upload', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/download"(controller: controllerName, action: 'download', method: HttpMethod.GET)
            }
        }


        group "/api/modelCatalogue/core/dataArchitect", {
            "/uninstantiatedDataElements" (controller: "dataArchitect", action: 'uninstantiatedDataElements', method: HttpMethod.GET)
            "/metadataKeyCheck/$key?" (controller: "dataArchitect", action: 'metadataKeyCheck', method: HttpMethod.GET)
            "/getSubModelElements/$modelId?" (controller: "dataArchitect", action: 'getSubModelElements', method: HttpMethod.GET)
            "/findRelationsByMetadataKeys/$key?" (controller: "dataArchitect", action: 'findRelationsByMetadataKeys', method: HttpMethod.GET)
        }

        "/"(view:"index")
        "/api/modelCatalogue/core/search/$search?" (controller:"search", action : 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataImports/upload" (controller: "dataImport", action: 'upload', method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataImports" (controller: "dataImport", action: 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataImport/$id" (controller: "dataImport", action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataImport/$id/pendingAction" (controller: "dataImport", action: 'pendingAction', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataImport/$id/importQueue" (controller: "dataImport", action: 'importQueue', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataImport/$id/imported" (controller: "dataImport", action: 'imported', method: HttpMethod.GET)

	}
}
