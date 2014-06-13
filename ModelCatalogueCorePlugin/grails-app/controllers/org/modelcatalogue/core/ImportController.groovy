package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.DataImport
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class ImportController extends AbstractRestfulController{

    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]
    def dataImportService

    ImportController() {
        super(DataImport, false)
    }

    def upload(Integer max) {
        def errorMsg
        DataImport importer
        setSafeMax(max)
        if (!(request instanceof MultipartHttpServletRequest)) return ["No File to process!"]
        String conceptualDomainName, conceptualDomainDescription
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request
        MultipartFile file = multiRequest.getFile("file")
        def params = multiRequest.getParameterMap()
        if (!params?.conceptualDomainName) {
            errorMsg = ["error": "No conceptual domain!"]
        }else if(!file){
            errorMsg = ["error": "No file"]
        }else {
            if (params?.conceptualDomainDescription) conceptualDomainDescription = params.conceptualDomainDescription.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim() else conceptualDomainDescription=""
            //Microsoft Excel files
            //Microsoft Excel 2007 files
            def okContentTypes = ['application/vnd.ms-excel','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream']
            def confType=file.getContentType()
            if (okContentTypes.contains(confType) && file.size > 0){
                    ExcelLoader parser = new ExcelLoader(file.inputStream)
                    def (headers, rows) = parser.parse()
                    HeadersMap headersMap = new HeadersMap()
                    headersMap = populateHeaders(headersMap)
                    importer = dataImportService.importData(headers, rows, conceptualDomainName, conceptualDomainDescription, headersMap)

            } else {
                if(!okContentTypes.contains(confType))
                    errorMsg = ["error":"Input should be an Excel file!\n"+
                            "but uploaded content is "+confType]
                else if (file.size<=0)
                    errorMsg = ["error":"The uploaded file is empty!"]
            }
            conceptualDomainName = params.conceptualDomainName.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim()
        }


        if(errorMsg) {
           respond errorMsg
        }

        respond importer
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
