package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.DataImport
import org.modelcatalogue.core.dataarchitect.ImportRow
import org.modelcatalogue.core.util.ImportRows
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class DataImportController extends AbstractRestfulController{

    def dataImportService
    private static final CONTENT_TYPES = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream']
    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]

    DataImportController() {
        super(DataImport, false)
    }

    @Override
    protected getBasePath() {
        return "/dataArchitect/imports"
    }

    def upload(Integer max) {
        def response
        DataImport importer
        handleParams(max)
        if (!(request instanceof MultipartHttpServletRequest)) {
            importer.errors.rejectValue('uploaded', 'import.uploadfailed', "No file")
        }else {
            String conceptualDomainName, conceptualDomainDescription, importName
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request
            MultipartFile file = multiRequest.getFile("file")
            def params = multiRequest.getParameterMap()
            if (!params?.conceptualDomain) {
                response = ["errors": "No conceptual domain!"]
            } else if (!params?.name) {
                response = ["errors": "no import name"]
            } else if (!file) {
                response = ["errors": "No file"]
            } else {
                conceptualDomainName = params.conceptualDomain.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim()
                if (params?.conceptualDomainDescription) conceptualDomainDescription = params.conceptualDomainDescription.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim() else conceptualDomainDescription = ""
                if (params?.name) importName = params.name.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim() else name = ""
                def confType = file.getContentType()
                if (CONTENT_TYPES.contains(confType) && file.size > 0) {
                    ExcelLoader parser = new ExcelLoader(file.inputStream)
                    def (headers, rows) = parser.parse()
                    HeadersMap headersMap = populateHeaders()
                    importer = dataImportService.importData(headers, rows, importName, conceptualDomainName, conceptualDomainDescription, headersMap)
                    response = importer

                } else {
                    if (!CONTENT_TYPES.contains(confType)) {
                        response = ["errors": "input should be an Excel file but uploaded content is ${confType}"]
                    } else if (file.size <= 0){
                        response = ["errors": "The uploaded file is empty"]
                    }
                }
            }
        }

        respond response
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
        respond response
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
        respond response
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
        respond response
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
        respond response
    }



    protected static HeadersMap populateHeaders(){
        HeadersMap headersMap = new HeadersMap()
        headersMap.dataElementCodeRow = "Data Item Unique Code"
        headersMap.dataElementNameRow = "Data Item Name"
        headersMap.dataElementDescriptionRow = "Data Item Description"
        headersMap.dataTypeRow = "Data type"
        headersMap.parentModelNameRow = "Parent Model"
        headersMap.parentModelCodeRow = "Parent Model Unique Code"
        headersMap.containingModelNameRow = "Model"
        headersMap.containingModelCodeRow = "Model Unique Code"
        headersMap.measurementUnitNameRow = "Measurement Unit"
        headersMap.metadataRow = "Metadata"
        return headersMap
    }
}
