package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.DataImport
import org.modelcatalogue.core.dataarchitect.ImportRow
import org.modelcatalogue.core.dataarchitect.xsd.XsdLoader
import org.modelcatalogue.core.util.ImportRows
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class DataImportController extends AbstractRestfulController{

    def dataImportService, XSDImportService
    private static final CONTENT_TYPES = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream', 'application/xml', 'text/xml']
    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]

    DataImportController() {
        super(DataImport, false)
    }

    @Override
    protected getBasePath() {
        return "/dataArchitect/imports"
    }


    protected getErrors(Map params, file){
        def errors = []
        if (!params?.conceptualDomain) errors.add("no conceptual domain!")
        if (!params?.name) errors.add("no import name")
        if (!file) errors.add("no file")
        return errors
    }

    protected trimString(string){
        string.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim()
        return string
    }



    def upload(Integer max) {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
        handleParams(max)
        def errors = [], response

        if (!(request instanceof MultipartHttpServletRequest)) {
            errors.add("No file")
            response =  ["errors": errors]
        }else {
            String conceptualDomainName, conceptualDomainDescription, importName
            MultipartFile file = request.getFile("file")
            errors.addAll(getErrors(params, file))

            if (!errors) {
                conceptualDomainName = trimString(params.conceptualDomain)
                if (params?.conceptualDomainDescription) conceptualDomainDescription = trimString(params.conceptualDomainDescription) else conceptualDomainDescription = ""
                if (params?.name) importName = trimString(params.name) else importName = ""
                def confType = file.getContentType()


                if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xls")) {
                    ExcelLoader parser = new ExcelLoader(file.inputStream)
                    def (headers, rows) = parser.parse()
                    HeadersMap headersMap = populateHeaders()
                    DataImport importer = dataImportService.importData(headers, rows, importName, conceptualDomainName, conceptualDomainDescription, headersMap)
                    response = importer

                } else if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xsd")) {

                    XsdLoader parserXSD = new XsdLoader(file.inputStream)
                    def (topLevelElements, simpleDataTypes, complexDataTypes, schema, logErrorsSACT) = parserXSD.parse()
                    XSDImportService.createAll(simpleDataTypes, complexDataTypes, topLevelElements, conceptualDomainName, conceptualDomainName, schema)

                    DataImport importer = new DataImport(name:conceptualDomainName).save(flush:true, failOnError:true)

                    response = importer

                } else {
                    if (!CONTENT_TYPES.contains(confType)) errors.add("input should be an Excel file but uploaded content is ${confType}")
                    if (file.size <= 0) errors.add("The uploaded file is empty")
                    response =  ["errors": errors]
                }
            }else{
                response =  ["errors": errors]
            }
        }



        reportCapableRespond response
    }


    def pendingAction(Integer max){
        handleParams(max)
        DataImport importer = queryForResource(params.id)
        def total = (importer?.pendingAction)? importer?.pendingAction.size() : 0
        def offset = 0
        List<ImportRow> items = []
        if (importer.pendingAction) items.addAll(importer?.pendingAction)
        respondWithLinks ImportRow, new ImportRows(
                base: "/dataArchitect/imports/${params.id}/pendingAction",
                items: items.subList(offset, Math.min( offset + params.max, total )),
                total: total
        )
    }

    def imported(Integer max){
        handleParams(max)
        DataImport importer = queryForResource(params.id)
        def total = (importer?.imported)? importer?.imported.size() : 0
        def offset = 0
        List<ImportRow> items = []
        if (importer.imported) items.addAll(importer?.imported)

        respondWithLinks ImportRow, new ImportRows(
                base: "/dataArchitect/imports/${params.id}/imported",
                items: items.subList(offset, Math.min( offset + params.max, total )),
                total: total
        )
    }

    def importQueue(Integer max){
        handleParams(max)
        DataImport importer = queryForResource(params.id)
        def total = (importer?.importQueue)? importer?.importQueue.size() : 0
        def offset = 0
        List<ImportRow> items = []
        if (importer.importQueue) items.addAll(importer?.importQueue)

        respondWithLinks ImportRow, new ImportRows(
                base: "/dataArchitect/imports/${params.id}/importQueue",
                items: items.subList(offset, Math.min( offset + params.max, total )),
                total: total
        )
    }

    def resolveAllRowActions(Long id, Long rowId){
        def response
        DataImport importer = queryForResource(params.id)
        ImportRow importRow = ImportRow.get(params.rowId)
        if(importer && importRow){
            dataImportService.resolveRow(importer, importRow)
            response = importer
        }else{
            response = ["error": "import or import row not found"]
        }
        reportCapableRespond response
    }

    def ingestRow(Long id, Long rowId){
        def response
        DataImport importer = queryForResource(params.id)
        ImportRow importRow = ImportRow.get(params.rowId)
        if(importer && importRow){
            dataImportService.ingestRow(importer, importRow)
            dataImportService.actionPendingModels(importer)
            response = importer
        }else{
            response = ["error": "import or import row not found"]
        }
        reportCapableRespond response
    }

    def resolveAll(Long id){
        def response
        DataImport importer = queryForResource(params.id)
        if(importer){
            dataImportService.resolveAllPendingRows(importer)
            response = importer
        }else{
            response = ["error": "import or import row not found"]
        }
        reportCapableRespond response
    }

    def ingestQueue(Long id){
        def response
        DataImport importer = queryForResource(params.id)
        if(importer){
            dataImportService.ingestImportQueue(importer)
            response = importer
        }else{
            response = ["error": "import or import row not found"]
        }
        reportCapableRespond response
    }



    protected static HeadersMap populateHeaders(){
        HeadersMap headersMap = new HeadersMap()
        headersMap.dataElementCode = "Data Item Unique Code"
        headersMap.dataElementName = "Data Item Name"
        headersMap.dataElementDescription = "Data Item Description"
        headersMap.dataType = "Data type"
        headersMap.parentModelName = "Parent Model"
        headersMap.parentModelCode = "Parent Model Unique Code"
        headersMap.containingModelName = "Model"
        headersMap.containingModelCode = "Model Unique Code"
        headersMap.measurementUnitName = "Measurement Unit"
        headersMap.measurementSymbol = "Measurement Unit Symbol"
        headersMap.classification = "Classification"
        headersMap.conceptualDomainName = "Conceptual Domain"
        headersMap.metadata = "Metadata"
        return headersMap
    }
}
