import org.springframework.http.HttpMethod

class ModelCatalogueCorePluginUrlMappings {


	static mappings = {

        "/catalogue/upload" (controller: "dataImport", action: 'upload', method: HttpMethod.POST)
        "/catalogue/$resource/$id(.${version})?" (controller: 'catalogue', action: 'xref', method: HttpMethod.GET)
        "/catalogue/$resource/$id(.${version})?/export" (controller: 'catalogue', action: 'xref', method: HttpMethod.GET)

        def resources         = ['batch', 'relationshipType', 'csvTransformation' ]
        def catalogueElements = ['asset', 'dataElement', 'model', 'catalogueElement', 'dataType', 'enumeratedType', 'measurementUnit', 'valueDomain', 'user', 'classification']
        def allElements       = catalogueElements + resources

        for (String controllerName in allElements) {
            "/api/modelCatalogue/core/$controllerName" (controller: controllerName, action: 'index', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName" (controller: controllerName, action: 'save', method: HttpMethod.POST)

            if (controllerName in catalogueElements) {
                "/api/modelCatalogue/core/$controllerName/uuid/$uuid" (controller: controllerName, action: 'uuid', method: HttpMethod.GET)
            }

            "/api/modelCatalogue/core/$controllerName/search/$search?" (controller: controllerName, action: 'search', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id/validate" (controller: controllerName, action: 'validate', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$controllerName/validate" (controller: controllerName, action: 'validate', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$controllerName/$id" (controller: controllerName, action: 'show', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$controllerName/$id" (controller: controllerName, action: 'update', method: HttpMethod.PUT)
            "/api/modelCatalogue/core/$controllerName/$id" (controller: controllerName, action: 'delete', method: HttpMethod.DELETE)


            if (controllerName == 'batch') {
                "/api/modelCatalogue/core/$controllerName/$id/archive"(controller: controllerName, action:  'archive', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/run"(controller: controllerName, action: 'runAll', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/actions/$state?"(controller: controllerName, action: 'listActions', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/actions/$actionId/dismiss"(controller: controllerName, action: 'dismiss', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/actions/$actionId/reactivate"(controller: controllerName, action: 'reactivate', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/actions/$actionId/run"(controller: controllerName, action: 'run', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/actions/$actionId/parameters"(controller: controllerName, action: 'updateActionParameters', method: HttpMethod.PUT)
                "/api/modelCatalogue/core/$controllerName/$id/actions/$actionId/dependsOn"(controller: controllerName, action: 'removeDependency', method: HttpMethod.DELETE)
                "/api/modelCatalogue/core/$controllerName/$id/actions/$actionId/dependsOn"(controller: controllerName, action: 'addDependency', method: HttpMethod.POST)
            }


            if (controllerName in catalogueElements) {
                "/api/modelCatalogue/core/$controllerName/$id/relationships" (controller: controllerName, action: "relationships", method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/relationships/search" (controller: controllerName, action: "searchRelationships", method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/relationships/$type/search" (controller: controllerName, action: "searchRelationships", method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/relationships/$type" (controller: controllerName, action: "relationships", method: HttpMethod.GET)
                // reordeing bidirectional relationships is not supported as the combined index is actually same for all group of related elements
                // and change from the other side would change the view from the opposite side
                // "/api/modelCatalogue/core/$controllerName/$id/relationships/$type" (controller: controllerName, action: "reorderCombined", method: HttpMethod.PUT)
                "/api/modelCatalogue/core/$controllerName/$id/outgoing/search" (controller: controllerName, action: 'searchOutgoing', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/outgoing/$type/search" (controller: controllerName, action: 'searchOutgoing', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/outgoing/$type" (controller: controllerName, action: 'outgoing', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/outgoing/$type" (controller: controllerName, action: 'addOutgoing', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/outgoing/$type" (controller: controllerName, action: 'removeOutgoing', method: HttpMethod.DELETE)
                "/api/modelCatalogue/core/$controllerName/$id/outgoing/$type" (controller: controllerName, action: 'reorderOutgoing', method: HttpMethod.PUT)
                "/api/modelCatalogue/core/$controllerName/$id/incoming/search" (controller: controllerName, action: 'searchIncoming', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/incoming/$type/search" (controller: controllerName, action: 'searchIncoming', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/incoming/$type" (controller: controllerName, action: 'incoming', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/incoming/$type" (controller: controllerName, action: 'addIncoming', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/incoming/$type" (controller: controllerName, action: 'removeIncoming', method: HttpMethod.DELETE)
                "/api/modelCatalogue/core/$controllerName/$id/incoming/$type" (controller: controllerName, action: 'reorderIncoming', method: HttpMethod.PUT)
                "/api/modelCatalogue/core/$controllerName/$id/incoming" (controller: controllerName, action: "incoming", method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/outgoing" (controller: controllerName, action: "outgoing", method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/mapping/$destination" (controller: controllerName, action: 'addMapping', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/mapping/$destination" (controller: controllerName, action: 'removeMapping', method: HttpMethod.DELETE)
                "/api/modelCatalogue/core/$controllerName/$id/mapping" (controller: controllerName, action: 'mappings', method: HttpMethod.GET)

                "/api/modelCatalogue/core/$controllerName/$id/history"(controller: controllerName, action: 'history', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$controllerName/$id/archive"(controller: controllerName, action: 'archive', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/restore"(controller: controllerName, action: 'restore', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$id/finalize"(controller: controllerName, action: 'finalizeElement', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$controllerName/$source/merge/$destination"(controller: controllerName, action: 'merge', method: HttpMethod.POST)

                if (controllerName == 'dataType' || controllerName == 'enumeratedType' ) {
                    "/api/modelCatalogue/core/$controllerName/$id/valueDomain"(controller: controllerName, action: 'valueDomains', method: HttpMethod.GET)
                }

                if (controllerName == 'classification') {
                    "/api/modelCatalogue/core/$controllerName/$id/classifies"(controller: controllerName, action: 'classifies', method: HttpMethod.GET)
                    // /ModelCatalogueCorePluginTestApp/api/modelCatalogue/core/classification/24/report
                    "/api/modelCatalogue/core/$controllerName/$id/report"(controller: controllerName, action: 'report', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$controllerName/$id/gereport"(controller: controllerName, action: 'gereport', method: HttpMethod.GET)
					"/api/modelCatalogue/core/$controllerName/$id/gereportDoc"(controller: controllerName, action: 'gereportDoc', method: HttpMethod.GET)
                }

                if (controllerName == 'measurementUnit') {
                    "/api/modelCatalogue/core/$controllerName/$id/valueDomain"(controller: controllerName, action: 'valueDomains', method: HttpMethod.GET)
                }

                if (controllerName == 'valueDomain') {
                    "/api/modelCatalogue/core/$controllerName/$id/dataElement"(controller: controllerName, action: 'dataElements', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$controllerName/$id/convert/$destination"(controller: controllerName, action: 'convert', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$controllerName/$id/validateValue"(controller: controllerName, action: 'validateValue', method: HttpMethod.GET)
                }

                if (controllerName == 'user') {
                    "/$controllerName/current"(controller: controllerName, action: 'current', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$controllerName/current"(controller: controllerName, action: 'current', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$controllerName/classifications"(controller: controllerName, action: 'classifications', method: HttpMethod.POST)
                }

                if (controllerName == 'asset') {
                    "/api/modelCatalogue/core/$controllerName/upload"(controller: controllerName, action: 'upload', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$controllerName/$id/upload"(controller: controllerName, action: 'upload', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$controllerName/$id/download"(controller: controllerName, action: 'download', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$controllerName/$id/content"(controller: controllerName, action: 'content', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$controllerName/$id/validateXml"(controller: controllerName, action: 'validateXml', method: HttpMethod.POST)
                }
            }

            if (controllerName == 'csvTransformation') {
                "/api/modelCatalogue/core/$controllerName/$id/transform"(controller: controllerName, action: 'transform', method: HttpMethod.POST)
            }

            if (controllerName == 'relationshipType') {
                "/api/modelCatalogue/core/$controllerName/elementClasses"(controller: controllerName, action: 'elementClasses', method: HttpMethod.GET)
            }
        }

        group "/api/modelCatalogue/core/dataArchitect", {
            "/uninstantiatedDataElements" (controller: "dataArchitect", action: 'uninstantiatedDataElements', method: HttpMethod.GET)
            "/metadataKeyCheck/$key?" (controller: "dataArchitect", action: 'metadataKeyCheck', method: HttpMethod.GET)
            "/getSubModelElements/$modelId?" (controller: "dataArchitect", action: 'getSubModelElements', method: HttpMethod.GET)
            "/findRelationsByMetadataKeys/$key?" (controller: "dataArchitect", action: 'findRelationsByMetadataKeys', method: HttpMethod.GET)
            "/elementsFromCSV" (controller: "dataArchitect", action: "elementsFromCSV", method: HttpMethod.POST)
            "/modelsFromCSV" (controller: "dataArchitect", action: "modelsFromCSV", method: HttpMethod.POST)
            "/generateSuggestions" (controller: "dataArchitect", action: "generateSuggestions", method: HttpMethod.POST)
            "/suggestionsNames" (controller: "dataArchitect", action: "suggestionsNames", method: HttpMethod.GET)
            "/imports/upload" (controller: "dataImport", action: 'upload', method: HttpMethod.POST)
        }

        "/"(view:"index")
        "/api/modelCatalogue/core/dashboard" (controller:"dashboard", action : 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/search/$search?" (controller:"search", action : 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/relationship/$id/restore" (controller:"relationship", action : 'restore', method: HttpMethod.POST)

	}
}
