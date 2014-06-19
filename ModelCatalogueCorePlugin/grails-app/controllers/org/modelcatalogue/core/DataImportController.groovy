package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.DataImport
import org.modelcatalogue.core.dataarchitect.ImportRow
import org.modelcatalogue.core.util.Elements
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

    def upload(Integer max) {
        DataImport importer
        setSafeMax(max)
        if (!(request instanceof MultipartHttpServletRequest)) {
            importer.errors.rejectValue('uploaded', 'import.uploadfailed', "No file")
        }else {
            String conceptualDomainName, conceptualDomainDescription, importName
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request
            MultipartFile file = multiRequest.getFile("file")
            def params = multiRequest.getParameterMap()
            if (!params?.conceptualDomain) {
                importer.errors.rejectValue('uploaded', 'import.uploadfailed', "No conceptual domain!")
            } else if (!params?.name) {
                importer.errors.rejectValue('uploaded', 'import.uploadfailed', "import name")
            } else if (!file) {
                importer.errors.rejectValue('uploaded', 'import.uploadfailed', "No file")
            } else {
                if (params?.conceptualDomainDescription) conceptualDomainDescription = params.conceptualDomainDescription.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim() else conceptualDomainDescription = ""
                if (params?.name) importName = params.name.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim() else name = ""
                def confType = file.getContentType()
                if (CONTENT_TYPES.contains(confType) && file.size > 0) {
                    ExcelLoader parser = new ExcelLoader(file.inputStream)
                    def (headers, rows) = parser.parse()
                    HeadersMap headersMap = populateHeaders()
                    importer = dataImportService.importData(headers, rows, importName, conceptualDomainName, conceptualDomainDescription, headersMap)

                } else {
                    if (!CONTENT_TYPES.contains(confType))
                        importer.errors.rejectValue('uploaded', 'import.uploadfailed', "\"error\":\"Input should be an Excel file!\\n\"+\n" +
                                "                            \"but uploaded content is \"+confType")
                    else if (file.size <= 0)
                        importer.errors.rejectValue('uploaded', 'import.uploadfailed', "The uploaded file is empty!")
                }
                conceptualDomainName = params.conceptualDomain.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim()
            }
        }

        respond importer
    }


    def pendingAction(Integer max){
        setSafeMax(max)
        DataImport importer = queryForResource(params.id)
        def total = (importer?.pendingAction)? importer?.pendingAction.size() : 0
        def offset = 0
        List<ImportRow> items = []
        if (importer.pendingAction) items.addAll(importer?.pendingAction)
        respondWithLinks ImportRow, new ImportRows(
                base: "/dataImport/${params.id}/pendingAction",
                items: items.subList(offset, Math.min( offset + params.max, total )),
                total: total
        )
    }

    def imported(Integer max){
        setSafeMax(max)
        DataImport importer = queryForResource(params.id)
        def total = (importer?.imported)? importer?.imported.size() : 0
        def offset = 0
        List<ImportRow> items = []
        if (importer.imported) items.addAll(importer?.imported)

        respondWithLinks ImportRow, new ImportRows(
                base: "/dataImport/${params.id}/pendingAction",
                items: items.subList(offset, Math.min( offset + params.max, total )),
                total: total
        )
    }

    def importQueue(Integer max){
        setSafeMax(max)
        DataImport importer = queryForResource(params.id)
        def total = (importer?.importQueue)? importer?.importQueue.size() : 0
        def offset = 0
        List<ImportRow> items = []
        if (importer.pendingAction) items.addAll(importer?.importQueue)

        respondWithLinks ImportRow, new ImportRows(
                base: "/dataImport/${params.id}/pendingAction",
                items: items.subList(offset, Math.min( offset + params.max, total )),
                total: total
        )
    }

    def resolveRow(Long id, Long rowId){
        DataImport importer = queryForResource(params.id)
        ImportRow importRow = ImportRow.get(params.rowId)
        def response =  [result: "success"]
        if(importer && importRow){
            dataImportService.resolveRow(importer, importRow)
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
