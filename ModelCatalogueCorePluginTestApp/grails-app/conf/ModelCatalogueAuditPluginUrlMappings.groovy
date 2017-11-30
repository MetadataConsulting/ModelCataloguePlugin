import org.springframework.http.HttpMethod

class ModelCatalogueAuditPluginUrlMappings {


    static mappings = {

        "/api/modelCatalogue/core/change/"(controller: 'change', action: 'global', method: HttpMethod.GET)
        "/api/modelCatalogue/core/change/$id"(controller: 'change', action: 'show', method: HttpMethod.GET)
        "/api/modelCatalogue/core/change/$id/changes"(controller: 'change', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/change/$id"(controller: 'change', action: 'undo', method: HttpMethod.DELETE)

        "/api/modelCatalogue/core/dataModel/$id/activity"(controller: 'change', action: 'dataModelActivity', method: HttpMethod.GET)
        "/api/modelCatalogue/core/user/$id/activity"(controller: 'change', action: 'userActivity', method: HttpMethod.GET)

        "/api/modelCatalogue/core/asset/$id/changes"(controller: 'asset', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataElement/$id/changes"(controller: 'dataElement', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataModel/$id/changes"(controller: 'dataModel', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/catalogueElement/$id/changes"(controller: 'catalogueElement', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataType/$id/changes"(controller: 'dataType', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/enumeratedType/$id/changes"(controller: 'enumeratedType', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/measurementUnit/$id/changes"(controller: 'measurementUnit', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/primitiveType/$id/changes"(controller: 'primitiveType', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/referenceType/$id/changes"(controller: 'referenceType', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/user/$id/changes"(controller: 'user', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataClass/$id/changes"(controller: 'dataClass', action: 'changes', method: HttpMethod.GET)
        "/api/modelCatalogue/core/validationRule/$id/changes"(controller: 'validationRule', action: 'changes', method: HttpMethod.GET)
    }
}
