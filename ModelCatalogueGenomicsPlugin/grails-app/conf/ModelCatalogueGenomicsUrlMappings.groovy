import org.springframework.http.HttpMethod;


class ModelCatalogueGenomicsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/gel/generateXmlShredderModel/$id"(controller: "gelXml", action: "generateXmlShredderModel", method: HttpMethod.GET)
        "/api/modelCatalogue/core/gel/generateXSD/$id"(controller: "gelXml", action: "generateXSD", method: HttpMethod.GET)
        "/api/modelCatalogue/core/gel/reports/classificationJasper"(controller: 'classificationReports', action: 'gereportDoc', method: HttpMethod.GET)
        "/api/modelCatalogue/core/gel/json/diseaseOntology/$id"(controller: "gelJson", action: "printDiseaseOntology", method: HttpMethod.GET)
    }

}