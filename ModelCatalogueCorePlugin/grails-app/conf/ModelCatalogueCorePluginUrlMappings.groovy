class ModelCatalogueCorePluginUrlMappings {


	static mappings = {
        group "/api/modelCatalogue/core", {
            "/$controller" {
                action = [GET: "index", POST: "save"]
            }
            "/$controller/search/$search?" {
                action = [GET: "search"]
            }
            "/$controller/validate" {
                action = [POST: "validate"]
            }
            "/$controller/$id/validate" {
                action = [POST: "validate"]
            }
            "/$controller/$id" {
                action = [GET: "show", PUT: "update", DELETE: "delete"]
            }
            "/$controller/$id/incoming" {
                action = [GET: "incoming"]
            }
            "/$controller/$id/outgoing" {
                action = [GET: "outgoing"]
            }
            "/$controller/$id/relationships" {
                action = [GET: "relationships"]
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
            "/$controller/$id/valueDomain" {
                action = [GET: "valueDomains"]
                constraints {
                    controller inList: ['dataType']
                }
            }

            constraints {
                controller inList: ['conceptualDomain', 'dataElement', 'dataType', 'enumeratedType', 'measurementUnit', 'model', 'valueDomain']
            }
        }

        "/api/modelCatalogue/core/search/$search" (controller:"search") {
            action = [GET: "index"]
        }

        group "/api/modelCatalogue/core/dataArchitect", {
            "/uninstantiatedDataElements" (controller:"dataArchitect"){
                action = [GET: "uninstantiatedDataElements"]
            }
            "/metadataKeyCheck/$key?" (controller:"dataArchitect"){
                action = [GET: "metadataKeyCheck"]
            }
        }


         "/"(view:"index")


	}
}
