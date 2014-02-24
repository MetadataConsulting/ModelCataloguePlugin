class ModelCatalogueCorePluginUrlMappings {


	static mappings = {
        group "/api/modelCatalogue/core", {
            "/$controller" {
                action = [GET: "index", POST: "save"]
            }
            "/$controller/$id" {
                action = [GET: "show", PUT: "update", DELETE: "delete"]
            }
            "/$controller/$id/outgoing" {
                action = [GET: "outgoing"]
            }
            "/$controller/$id/incoming" {
                action = [GET: "incoming"]
            }
            "/$controller/$id/outgoing/$type" {
                action = [GET: "outgoing", POST: "addOutgoing", DELETE: "removeOutgoing"]
            }
            "/$controller/$id/incoming/$type" {
                action = [GET: "incoming", POST: "addIncoming", DELETE: "removeIncoming"]
            }

            constraints {
                controller inList: ['conceptualDomain', 'dataElement', 'dataType', 'enumeratedType', 'measurementUnit', 'model', 'valueDomain']
            }
        }

	}
}
