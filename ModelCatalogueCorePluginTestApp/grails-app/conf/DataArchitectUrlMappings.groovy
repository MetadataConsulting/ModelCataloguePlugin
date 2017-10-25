import org.springframework.http.HttpMethod

class DataArchitectUrlMappings {
    static mappings = {
        // DataArchitect
        "/api/modelCatalogue/core/dataArchitect/metadataKeyCheck/$key?"(controller: "dataArchitect", action: 'metadataKeyCheck', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataArchitect/getSubModelElements/$modelId?"(controller: "dataArchitect", action: 'getSubModelElements', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataArchitect/findRelationsByMetadataKeys/$key?"(controller: "dataArchitect", action: 'findRelationsByMetadataKeys', method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataArchitect/elementsFromCSV"(controller: "dataArchitect", action: "elementsFromCSV", method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataArchitect/modelsFromCSV"(controller: "dataArchitect", action: "modelsFromCSV", method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataArchitect/generateSuggestions"(controller: "dataArchitect", action: "generateSuggestions", method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataArchitect/deleteSuggestions"(controller: "dataArchitect", action: "deleteSuggestions", method: HttpMethod.POST)
        "/api/modelCatalogue/core/dataArchitect/suggestionsNames"(controller: "dataArchitect", action: "suggestionsNames", method: HttpMethod.GET)
        "/api/modelCatalogue/core/dataArchitect/imports/upload"(controller: "dataImport", action: 'upload', method: HttpMethod.POST)
    }
}