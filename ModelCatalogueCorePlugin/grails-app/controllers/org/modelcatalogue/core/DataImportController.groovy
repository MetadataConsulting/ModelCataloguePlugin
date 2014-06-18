package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.DataImport
import org.modelcatalogue.core.dataarchitect.ImportRow
import org.modelcatalogue.core.util.ImportRows
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class DataImportController extends AbstractRestfulController{

    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]
    def dataImportService

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

                //Microsoft Excel files
                //Microsoft Excel 2007 files
                def okContentTypes = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream']
                def confType = file.getContentType()
                if (okContentTypes.contains(confType) && file.size > 0) {
                    ExcelLoader parser = new ExcelLoader(file.inputStream)
                    def (headers, rows) = parser.parse()
                    HeadersMap headersMap = new HeadersMap()
                    headersMap = populateHeaders(headersMap)
                    importer = dataImportService.importData(headers, rows, importName, conceptualDomainName, conceptualDomainDescription, headersMap)

                } else {
                    if (!okContentTypes.contains(confType))
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


    def pendingAction(){
        DataImport importer = queryForResource(params.id)
        def total = (importer?.pendingAction)? importer?.pendingAction.size() : 0

        List<ImportRow> items = importer.pendingAction

        respondWithLinks ImportRow, new ImportRows(
                base: "/dataImport/${params.id}/pendingAction",
                items: importer.pendingAction,
                total: total
        )

    }

    def imported(){
        DataImport importer = queryForResource(params.id)
        def total = (importer?.imported)? importer?.imported.size() : 0

        def test = new ImportRows(
                base: "/dataImport/${params.id}/pendingAction",
                items: importer.pendingAction,
                total: total
        )


        respondWithLinks ImportRow, new ImportRows(
                base: "/dataImport/${params.id}/pendingAction",
                items: importer.pendingAction,
                total: total
        )
    }

    def importQueue(){
        DataImport importer = queryForResource(params.id)
        def total = (importer?.importQueue)? importer?.importQueue.size() : 0
        respondWithLinks ImportRow, new ImportRows(
                base: "/dataImport/${params.id}/pendingAction",
                items: importer.pendingAction,
                total: total
        )
    }



    protected static HeadersMap populateHeaders(HeadersMap headersMap){
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
