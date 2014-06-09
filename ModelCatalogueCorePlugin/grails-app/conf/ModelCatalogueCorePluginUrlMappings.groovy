class ModelCatalogueCorePluginUrlMappings {


	static mappings = {
        group "/api/modelCatalogue/core", {
            "/$controller(.$format)?" {
                action = [GET: "index", POST: "save"]
            }
            "/$controller/search/$search?(.$format)?" {
                action = [GET: "search"]
            }
            "/$controller/validate(.$format)?" {
                action = [POST: "validate"]
            }
            "/$controller/$id/validate(.$format)?" {
                action = [POST: "validate"]
            }
            "/$controller/$id(.$format)?" {
                action = [GET: "show", PUT: "update", DELETE: "delete"]
            }
            "/$controller/$id/incoming(.$format)?" {
                action = [GET: "incoming"]
            }
            "/$controller/$id/outgoing(.$format)?" {
                action = [GET: "outgoing"]
            }
            "/$controller/$id/relationships(.$format)?" {
                action = [GET: "relationships"]
            }
            "/$controller/$id/outgoing/$type(.$format)?" {
                action = [GET: "outgoing", POST: "addOutgoing", DELETE: "removeOutgoing"]
            }
            "/$controller/$id/incoming/$type(.$format)?" {
                action = [GET: "incoming", POST: "addIncoming", DELETE: "removeIncoming"]
            }
            "/$controller/$id/mapping/$destination(.$format)?" {
                action = [POST: "addMapping", DELETE: "removeMapping"]
                constraints {
                    controller inList: ['valueDomain']
                }
            }
            "/$controller/$id/valueDomain(.$format)?" {
                action = [GET: "valueDomains"]
                constraints {
                    controller inList: ['dataType']
                }
            }
            "/$controller/$id/mapping(.$format)?" {
                action = [GET: "mappings"]
            }

            "/$controller/$id/history(.$format)?" {
                action = [GET: "history"]
            }

            constraints {
                controller inList: ['conceptualDomain', 'dataElement', 'dataType', 'enumeratedType', 'measurementUnit', 'model', 'valueDomain']
            }
        }

        "/api/modelCatalogue/core/search/$search(.$format)?" (controller:"search") {
            action = [GET: "index"]
        }

        group "/api/modelCatalogue/core/dataArchitect", {
            "/uninstantiatedDataElements(.$format)?" (controller:"dataArchitect"){
                action = [GET: "uninstantiatedDataElements"]
            }
            "/metadataKeyCheck/$key?(.$format)?" (controller:"dataArchitect"){
                action = [GET: "metadataKeyCheck"]
            }
            "/getSubModelElements/$modelId?(.$format)?" (controller:"dataArchitect"){
                action = [GET: "getSubModelElements"]
            }
            "/findRelationsByMetadataKeys/$key?(.$format)?" (controller:"dataArchitect"){
                action = [GET: "findRelationsByMetadataKeys"]
            }

        }


         "/"(view:"index")


	}
}
