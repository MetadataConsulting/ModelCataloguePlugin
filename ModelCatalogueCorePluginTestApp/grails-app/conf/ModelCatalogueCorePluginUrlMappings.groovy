import org.springframework.http.HttpMethod

class ModelCatalogueCorePluginUrlMappings {


	static mappings = {

        "/api/modelCatalogue/core/forms/generate/$id" (controller: 'formGenerator', action: 'generateForm', method: HttpMethod.GET)
        "/api/modelCatalogue/core/forms/preview/$id" (controller: 'formGenerator', action: 'previewForm', method: HttpMethod.GET)

        "/catalogue/upload" (controller: "dataImport", action: 'upload', method: HttpMethod.POST)
        "/catalogue/ext/$key/$value" (controller: 'catalogue', action: 'ext', method: HttpMethod.GET)
        "/catalogue/ext/$key/$value/export" (controller: 'catalogue', action: 'ext', method: HttpMethod.GET)
        "/catalogue/$resource/$id(.${version})?" (controller: 'catalogue', action: 'xref', method: HttpMethod.GET)
        "/catalogue/$resource/$id(.${version})?/export" (controller: 'catalogue', action: 'xref', method: HttpMethod.GET) {
            format = 'xml'
        }

        /// cytoscape stuff:
        // JSON export for cytoscape graph
        "/catalogue/$resource/$id(.${version})/cytoscapeJsonExport" (controller: 'catalogue', action: 'cytoscape_json', method: HttpMethod.GET) // { format = 'json' }
        // view graph as catalogue
        "/catalogue/cytoscapeGraphView" (controller: 'catalogue', 'action': 'display_cytoscape', method: HttpMethod.GET)
        // get ID-model map for model_catalogue_graph's get model list function
        "/catalogue/getIdModelMap" (controller: 'dataModel', action: 'idModelMap', method: HttpMethod.GET) { format = 'json' } // to get data model list as Json

        "/api/modelCatalogue/core/feedback"(controller: 'catalogue', action: 'feedbacks', method: HttpMethod.GET)
        "/api/modelCatalogue/core/feedback/$key"(controller: 'catalogue', action: 'feedback', method: HttpMethod.GET)
        "/api/modelCatalogue/core/logs"(controller: 'logging', action: 'logsToAssets', method: HttpMethod.GET)

        def legacyElements    = [model: 'dataClass', classification: 'dataModel']
        def resources         = ['batch', 'relationshipType', 'csvTransformation', 'dataModelPolicy' ]
        def catalogueElements = ['asset', 'dataElement', 'dataClass', 'catalogueElement', 'dataType', 'enumeratedType', 'referenceType', 'primitiveType', 'measurementUnit', 'user', 'dataModel', 'classification', 'model', 'validationRule', 'tag']
        def allElements       = catalogueElements + resources

        for (String elementName in allElements) {
            String controllerName = elementName
            if (elementName in legacyElements.keySet()) {
                controllerName = legacyElements[elementName]
            }

            if (controllerName == 'tag') {
                "/api/modelCatalogue/core/$elementName/forDataModel/$dataModelId"(controller: controllerName, action: 'forDataModel', method: HttpMethod.GET)
            }

            "/api/modelCatalogue/core/$elementName" (controller: controllerName, action: 'index', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$elementName" (controller: controllerName, action: 'save', method: HttpMethod.POST)

            "/api/modelCatalogue/core/$elementName/search/$search?" (controller: controllerName, action: 'search', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$elementName/$id/validate" (controller: controllerName, action: 'validate', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$elementName/$id/setDeprecated" (controller: controllerName, action: 'setDeprecated', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$elementName/validate" (controller: controllerName, action: 'validate', method: HttpMethod.POST)
            "/api/modelCatalogue/core/$elementName/$id" (controller: controllerName, action: 'show', method: HttpMethod.GET)
            "/api/modelCatalogue/core/$elementName/$id" (controller: controllerName, action: 'update', method: HttpMethod.PUT)
            "/api/modelCatalogue/core/$elementName/$id" (controller: controllerName, action: 'delete', method: HttpMethod.DELETE)


            if (controllerName == 'batch') {
                "/api/modelCatalogue/core/$elementName/$id/archive"(controller: controllerName, action:  'archive', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/run"(controller: controllerName, action: 'runAll', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/actions/$state?"(controller: controllerName, action: 'listActions', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/actions/$actionId/dismiss"(controller: controllerName, action: 'dismiss', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/actions/$actionId/reactivate"(controller: controllerName, action: 'reactivate', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/actions/$actionId/run"(controller: controllerName, action: 'run', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/actions/$actionId/parameters"(controller: controllerName, action: 'updateActionParameters', method: HttpMethod.PUT)
                "/api/modelCatalogue/core/$elementName/$id/actions/$actionId/dependsOn"(controller: controllerName, action: 'removeDependency', method: HttpMethod.DELETE)
                "/api/modelCatalogue/core/$elementName/$id/actions/$actionId/dependsOn"(controller: controllerName, action: 'addDependency', method: HttpMethod.POST)
            }


            if (controllerName in catalogueElements) {
                "/api/modelCatalogue/core/$elementName/$id/outgoing/search" (controller: controllerName, action: 'searchOutgoing', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/outgoing/$type/search" (controller: controllerName, action: 'searchOutgoing', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/outgoing/$type" (controller: controllerName, action: 'outgoing', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/outgoing/$type" (controller: controllerName, action: 'addOutgoing', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/outgoing/$type" (controller: controllerName, action: 'removeOutgoing', method: HttpMethod.DELETE)
                "/api/modelCatalogue/core/$elementName/$id/outgoing/$type" (controller: controllerName, action: 'reorderOutgoing', method: HttpMethod.PUT)
                "/api/modelCatalogue/core/$elementName/$id/incoming/search" (controller: controllerName, action: 'searchIncoming', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/incoming/$type/search" (controller: controllerName, action: 'searchIncoming', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/incoming/$type" (controller: controllerName, action: 'incoming', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/incoming/$type" (controller: controllerName, action: 'addIncoming', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/incoming/$type" (controller: controllerName, action: 'removeIncoming', method: HttpMethod.DELETE)
                "/api/modelCatalogue/core/$elementName/$id/incoming/$type" (controller: controllerName, action: 'reorderIncoming', method: HttpMethod.PUT)
                "/api/modelCatalogue/core/$elementName/$id/incoming" (controller: controllerName, action: "incoming", method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/outgoing" (controller: controllerName, action: "outgoing", method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/mapping/$destination" (controller: controllerName, action: 'addMapping', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/mapping/$destination" (controller: controllerName, action: 'removeMapping', method: HttpMethod.DELETE)
                "/api/modelCatalogue/core/$elementName/$id/mapping" (controller: controllerName, action: 'mappings', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/typeHierarchy" (controller: controllerName, action: 'typeHierarchy', method: HttpMethod.GET)

                "/api/modelCatalogue/core/$elementName/$id/history"(controller: controllerName, action: 'history', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/path"(controller: controllerName, action: 'path', method: HttpMethod.GET)
                "/api/modelCatalogue/core/$elementName/$id/archive"(controller: controllerName, action: 'archive', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/restore"(controller: controllerName, action: 'restore', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/finalize"(controller: controllerName, action: 'finalizeElement', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$id/clone/$destinationDataModelId"(controller: controllerName, action: 'cloneElement', method: HttpMethod.POST)
                "/api/modelCatalogue/core/$elementName/$source/merge/$destination"(controller: controllerName, action: 'merge', method: HttpMethod.POST)

                if (controllerName == 'dataType' || controllerName == 'enumeratedType' || controllerName == 'referenceType' || controllerName == 'primitiveType') {
                    "/api/modelCatalogue/core/$elementName/$id/dataElement"(controller: controllerName, action: 'dataElements', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/convert/$destination"(controller: controllerName, action: 'convert', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/validateValue"(controller: controllerName, action: 'validateValue', method: HttpMethod.GET)
                }

                if (controllerName in ['dataModel', 'classification']) {
                    "/api/modelCatalogue/core/$elementName/preload"(controller: 'catalogue', action: 'dataModelsForPreload', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/preload"(controller: 'catalogue', action: 'importFromUrl', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/$id/declares"(controller: controllerName, action: 'declares', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/containsOrImports/$other"(controller: controllerName, action: 'containsOrImports', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/content"(controller: controllerName, action: 'content', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/newVersion"(controller: controllerName, action: 'newVersion', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/$id/inventorySpreadsheet"(controller: controllerName, action: 'inventorySpreadsheet', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/inventoryDoc"(controller: controllerName, action: 'inventoryDoc', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/dependents"(controller: controllerName, action: 'dependents', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/reindex"(controller: controllerName, action: 'reindex', method: HttpMethod.POST)
                }

                if (controllerName == 'measurementUnit') {
                    "/api/modelCatalogue/core/$elementName/$id/primitiveType"(controller: controllerName, action: 'primitiveTypes', method: HttpMethod.GET)
                }

                if (controllerName == 'dataClass') {
                    "/api/modelCatalogue/core/$elementName/$id/inventoryDoc"(controller: 'dataClass', action: 'inventoryDoc', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/classificationChangelog"(controller: 'dataClass', action: 'changelogDoc', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/inventorySpreadsheet"(controller: 'dataClass', action: 'inventorySpreadsheet', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/referenceType"(controller: controllerName, action: 'referenceTypes', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/content"(controller: controllerName, action: 'content', method: HttpMethod.GET)

                }

                if (controllerName == 'user') {
                    "/$controllerName/current"(controller: controllerName, action: 'current', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/current"(controller: controllerName, action: 'current', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/classifications"(controller: controllerName, action: 'classifications', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/lastSeen"(controller: controllerName, action: 'lastSeen', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/apikey"(controller: controllerName, action: 'apiKey', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/$id/favourite"(controller: controllerName, action: 'addFavourite', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/$id/favourite"(controller: controllerName, action: 'removeFavourite', method: HttpMethod.DELETE)
                    "/api/modelCatalogue/core/$elementName/$id/enable"(controller: controllerName, action: 'enable', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/$id/disable"(controller: controllerName, action: 'disable', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/$id/role/$role"(controller: controllerName, action: 'role', method: HttpMethod.POST)
                }

                if (controllerName == 'asset') {
                    "/api/modelCatalogue/core/$elementName/upload"(controller: controllerName, action: 'upload', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/$id/upload"(controller: controllerName, action: 'upload', method: HttpMethod.POST)
                    "/api/modelCatalogue/core/$elementName/$id/download"(controller: controllerName, action: 'download', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/content"(controller: controllerName, action: 'content', method: HttpMethod.GET)
                    "/api/modelCatalogue/core/$elementName/$id/validateXml"(controller: controllerName, action: 'validateXml', method: HttpMethod.POST)
                }

                if (controllerName in ['dataElement', 'primitiveType', 'referenceType', 'enumeratedType', 'validationRule']) {
                    "/api/modelCatalogue/core/$elementName/$id/content"(controller: controllerName, action: 'content', method: HttpMethod.GET)
                }
            }

            if (controllerName == 'csvTransformation') {
                "/api/modelCatalogue/core/$elementName/$id/transform"(controller: controllerName, action: 'transform', method: HttpMethod.POST)
            }

            if (controllerName == 'relationshipType') {
                "/api/modelCatalogue/core/$elementName/elementClasses"(controller: controllerName, action: 'elementClasses', method: HttpMethod.GET)
            }
        }

        group "/api/modelCatalogue/core/dataArchitect", {
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
        "/api/modelCatalogue/core/search/reindex" (controller:"search", action : 'reindex', method: HttpMethod.POST)
        "/api/modelCatalogue/core/search/$search?" (controller:"search", action : 'index', method: HttpMethod.GET)
        "/api/modelCatalogue/core/relationship/$id/restore" (controller:"relationship", action : 'restore', method: HttpMethod.POST)

	}
}
