package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.DataImport
import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.ImportRow
import org.modelcatalogue.core.dataarchitect.xsd.XsdLoader
import org.modelcatalogue.core.util.ImportRows
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class DataImportController extends AbstractRestfulController<DataImport> {

    def dataImportService
    def initCatalogueService
    def XSDImportService
    def OBOService

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


    protected static getErrors(Map params, MultipartFile file) {
        def errors = []
        if (!file) {
            if (!params?.conceptualDomain) errors.add("no conceptual domain!")
        } else if (!file.originalFilename.endsWith('.obo') && !file.originalFilename.endsWith('.mc') ) {
            if (!params?.conceptualDomain) errors.add("no conceptual domain!")
        }
        if (!params?.name) errors.add("no import name")
        if (!file) errors.add("no file")
        return errors
    }

    protected static trimString(string) {
        string.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim()
        return string
    }



    def upload(Integer max) {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
        handleParams(max)


        if (!(request instanceof MultipartHttpServletRequest)) {
            respond "errors": [message: 'No file selected']
            return
        }

        def errors = [], response

        String conceptualDomainName, conceptualDomainDescription, importName
        MultipartFile file = request.getFile("file")
        errors.addAll(getErrors(params, file))

        if (errors) {
            response = ["errors": errors]
            respond response
            return
        }

        conceptualDomainName = trimString(params.conceptualDomain)
        if (params?.conceptualDomainDescription) conceptualDomainDescription = trimString(params.conceptualDomainDescription) else conceptualDomainDescription = ""
        if (params?.name) importName = trimString(params.name) else importName = ""
        def confType = file.getContentType()


        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xls")) {

            InputStream inputStream = file.inputStream
            def asset = storeAsset(params, file)
            ExcelLoader parser = new ExcelLoader(inputStream)
            def (headers, rows) = parser.parse()
            HeadersMap headersMap = populateHeaders(objectToBind.headersMap ?: [:])
            DataImport importer = dataImportService.importData(headers, rows, importName, conceptualDomainName, conceptualDomainDescription, headersMap, asset)
            response = importer
            respond response
            return
        }

        if (file.size > 0 && file.originalFilename.endsWith(".obo")) {
            def asset = storeAsset(params, file, 'text/obo')
            def id = asset.id
            InputStream inputStream = file.inputStream
            String name = params?.name
            String idpattern = params.idpattern
            executorService.submit {
                try {
                    Classification classification = OBOService.importOntology(inputStream, name, idpattern)
                    Asset updated = Asset.get(id)
                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your import has finished."
                    updated.save(flush: true, failOnError: true)
                    updated.addToClassifications(classification)
                    classification.addToClassifies(updated)
                    if (classification) {
                        updated.addToRelatedTo(classification)
                    }
                } catch (Exception e) {
                    Asset updated = Asset.get(id)
                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during upload"
                    updated.description = "Error importing obo file: ${e}"
                    updated.save(flush: true, failOnError: true)
                }
            }

            webRequest.currentResponse.with {
                //TODO: remove the base link
                def location = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/api/modelCatalogue/core/asset/" + asset.id
                status = 302
                setHeader("Location", location.toString())
                setHeader("X-Asset-ID", asset.id.toString())
                outputStream.flush()
            }
            return
        }

        if (file.size > 0 && file.originalFilename.endsWith(".mc")) {
            def asset = storeAsset(params, file, 'application/model-catalogue')
            def id = asset.id
            InputStream inputStream = file.inputStream

//            executorService.submit {
                try {
                    Set<CatalogueElement> created = initCatalogueService.importMCFile(inputStream)

                    Asset updated = Asset.get(id)
                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your import has finished."
                    updated.save(flush: true, failOnError: true)

                    Classification classification = created.find { it instanceof Classification } as Classification

                    if (classification) {
                        updated.addToClassifications(classification)
                        classification.addToClassifies(updated)
                        updated.addToRelatedTo(classification)
                    }
                } catch (Exception e) {
                    Asset updated = Asset.get(id)
                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during upload"
                    updated.description = "Error importing obo file: ${e}"
                    updated.save(flush: true, failOnError: true)
                }
//            }

            redirect url: "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/api/modelCatalogue/core/asset/" + asset.id
            return
        }

        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xsd")) {

            Asset asset = renderImportAsAsset(params, file, conceptualDomainName)

            webRequest.currentResponse.with {
                //TODO: remove the base link
                def location = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/api/modelCatalogue/core/asset/" + asset.id
                status = 302
                setHeader("Location", location.toString())
                setHeader("X-Asset-ID", asset.id.toString())
                outputStream.flush()
            }
            return

        }

        if (!CONTENT_TYPES.contains(confType)) errors.add("input should be an Excel file but uploaded content is ${confType}")
        if (file.size <= 0) errors.add("The uploaded file is empty")
        response = ["errors": errors]
        respond response
    }


    protected storeAsset(param, file, contentType = 'application/xslt'){

        String theName = (param.name ?: param.action)

        Asset asset = new Asset(
                name: "Import for " + theName,
                originalFileName: theName,
                description: "Your import will be available in this asset soon. Use Refresh action to reload.",
                status: ElementStatus.PENDING,
                contentType: contentType,
                size: 0
        )
        assetService.storeAssetFromFile(file, asset)
        asset.save(flush: true, failOnError: true)

        return asset

    }

    protected renderImportAsAsset(param, file, conceptualDomainName){

        String uri = request.forwardURI + '?' + request.queryString
        InputStream inputStream = file.inputStream
        def asset = storeAsset(param, file)
        Long id = asset.id
        Boolean createModelsForElements = params.boolean('createModelsForElements')

        executorService.submit {
            Asset updated = Asset.get(id)
            try {
                XsdLoader parserXSD = new XsdLoader(inputStream)
                def (topLevelElements, simpleDataTypes, complexDataTypes, schema, namespaces) = parserXSD.parse()
                def (classification, conceptualDomain) = XSDImportService.createAll(simpleDataTypes, complexDataTypes, topLevelElements, conceptualDomainName, conceptualDomainName, schema, namespaces, createModelsForElements)
                updated.status = ElementStatus.FINALIZED
                updated.description = "Your export is ready. Use Download button to view it."
                updated.ext['Original URL'] = uri
                updated.save(flush: true, failOnError: true)
                updated.addToRelatedTo(classification)
                updated.addToRelatedTo(conceptualDomain)
            } catch (e) {
                log.error("Error importing schema", e)
                updated.refresh()
                updated.status = ElementStatus.FINALIZED
                updated.name = updated.name + " - Error during upload"
                updated.description = "Error importing file: please validate that the schema is valid xml and that any dependencies already exist in the catalogue"
                updated.save(flush: true, failOnError: true)
            }
        }

        asset

    }





    def pendingAction(Integer max){
        handleParams(max)
        DataImport importer = queryForResource(params.id)
        int total = (importer?.pendingAction) ? importer?.pendingAction?.size() : 0
        int offset = 0
        List<ImportRow> items = []
        if (importer.pendingAction) items.addAll(importer?.pendingAction)
        respondWithLinks ImportRow, new ImportRows(
                base: "/dataArchitect/imports/${params.id}/pendingAction",
                items: items.subList(offset, Math.min(offset + params.int('max'), total)),
                total: total
        )
    }

    def imported(Integer max){
        handleParams(max)
        DataImport importer = queryForResource(params.id)
        int total = (importer?.imported) ? importer?.imported?.size() : 0
        int offset = 0
        List<ImportRow> items = []
        if (importer.imported) items.addAll(importer?.imported)

        respondWithLinks ImportRow, new ImportRows(
                base: "/dataArchitect/imports/${params.id}/imported",
                items: items.subList(offset, Math.min(offset + params.int('max'), total)),
                total: total
        )
    }

    def importQueue(Integer max){
        handleParams(max)
        DataImport importer = queryForResource(params.id)
        int total = (importer?.importQueue) ? importer?.importQueue?.size() : 0
        int offset = 0
        List<ImportRow> items = []
        if (importer.importQueue) items.addAll(importer?.importQueue)

        respondWithLinks ImportRow, new ImportRows(
                base: "/dataArchitect/imports/${params.id}/importQueue",
                items: items.subList(offset, Math.min(params.int('max'), total)),
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



    protected static HeadersMap populateHeaders(params){

        HeadersMap headersMap = new HeadersMap()



        headersMap.dataElementCode = params.dataElementCode ?: "Data Item Unique Code"
        headersMap.dataElementName = params.dataElementName ?: "Data Item Name"
        headersMap.dataElementDescription = params.dataElementDescription ?: "Data Item Description"
        headersMap.dataType = params.dataType ?: "Data type"
        headersMap.parentModelName = params.parentModelName ?: "Parent Model"
        headersMap.parentModelCode = params.parentModelCode ?: "Parent Model Unique Code"
        headersMap.containingModelName = params.containingModelName ?: "Model"
        headersMap.containingModelCode = params.containingModelCode ?: "Model Unique Code"
        headersMap.measurementUnitName = params.measurementUnitName ?: "Measurement Unit"
        headersMap.measurementSymbol = params.measurementSymbol ?: "Measurement Unit Symbol"
        headersMap.classification = params.classification ?: "Classification"
        headersMap.metadata = params.metadata ?: "Metadata"
        return headersMap
    }
}
