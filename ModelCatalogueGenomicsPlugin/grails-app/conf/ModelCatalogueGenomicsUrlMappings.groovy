import org.springframework.http.HttpMethod;


class ModelCatalogueGenomicsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/gel/generateXmlShredderModel/$id"(controller: "xml", action: "generateXmlShredderModel", method: HttpMethod.GET)
        "/api/modelCatalogue/core/gel/generateXSD/$id"(controller: "xml", action: "generateXSD", method: HttpMethod.GET)
    }

}