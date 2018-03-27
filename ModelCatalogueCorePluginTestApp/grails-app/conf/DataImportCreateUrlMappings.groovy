import org.springframework.http.HttpMethod

class DataImportCreateUrlMappings {

    static mappings = {
        "/dataImport/obo"(controller: 'dataImportCreate', action: 'importObo', method: HttpMethod.GET)
        "/dataImport/dsl"(controller: 'dataImportCreate', action: 'importModelCatalogueDSL', method: HttpMethod.GET)
        "/dataImport/excel"(controller: 'dataImportCreate', action: 'importExcel', method: HttpMethod.GET)
        "/dataImport/xml"(controller: 'dataImportCreate', action: 'importXml', method: HttpMethod.GET)
    }
}