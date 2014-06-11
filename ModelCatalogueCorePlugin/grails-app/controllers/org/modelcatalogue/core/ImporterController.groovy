package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.Importer
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class ImporterController {

    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]
    def dataImportService

    def upload()
    {
        def message
        if(!(request instanceof MultipartHttpServletRequest)) return ["No File to process!"]
        String conceptualDomainName, conceptualDomainDescription
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request
        MultipartFile  file = multiRequest.getFile("file")
        def params = multiRequest.getParameterMap()
        if (!params?.conceptualDomainName) return ["No conceptual domain!"] else conceptualDomainName = params.conceptualDomainName.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim()
        if (params.conceptualDomainDescription) conceptualDomainDescription = params.conceptualDomainDescription.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim() else conceptualDomainDescription=""
        //Microsoft Excel files
        //Microsoft Excel 2007 files
        def okContentTypes = ['application/vnd.ms-excel','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream']
        def confType=file.getContentType()
        if (okContentTypes.contains(confType) && file.size > 0){
                ExcelLoader parser = new ExcelLoader(file.inputStream)
                def (headers, rows) = parser.parse()
                HeadersMap headersMap = new HeadersMap()
                headersMap = populateHeaders(headersMap)
                Importer importer = dataImportService.importData(headers, rows, conceptualDomainName, conceptualDomainDescription, headersMap)
                respond importer

        } else {
            if(!okContentTypes.contains(confType))
                message ="Input should be an Excel file!\n"+
                        "but uploaded content is "+confType
            else if (file.size<=0)
                message ="The uploaded file is empty!"
        }
        respond message
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
