class ModelCatalogueCorePluginUrlMappings {


	static mappings = {
        group "/api/modelCatalogue/core", {
            "/$controller" {
                action = [GET: "index", POST: "save"]
            }
            "/$controller/$id" {
                action = [GET: "show", PUT: "update", DELETE: "delete"]
            }
            "/$controller/$id/validate" {
                action = [POST: "validate"]
            }
            "/$controller/$id/incoming" {
                action = [GET: "incoming"]
            }
            "/$controller/$id/outgoing" {
                action = [GET: "outgoing"]
            }
            "/$controller/$id/outgoing/$type" {
                action = [GET: "outgoing", POST: "addOutgoing", DELETE: "removeOutgoing"]
            }
            "/$controller/$id/incoming/$type" {
                action = [GET: "incoming", POST: "addIncoming", DELETE: "removeIncoming"]
            }
            "/$controller/$id/mapping" {
                action = [GET: "mappings"]
                constraints {
                    controller inList: ['valueDomain']
                }
            }

            "/$controller/$id/mapping/$destination" {
                action = [POST: "addMapping", DELETE: "removeMapping"]
                constraints {
                    controller inList: ['valueDomain']
                }
            }

            constraints {
                controller inList: ['conceptualDomain', 'dataElement', 'dataType', 'enumeratedType', 'measurementUnit', 'model', 'valueDomain']
            }
        }

	}
}
