package org.modelcatalogue.core


import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap

import org.modelcatalogue.core.dataarchitect.xsd.XsdLoader
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class DataImportController  {

    def dataImportService
    def initCatalogueService
    def XSDImportService
    def OBOService
    def umljService
    def modelCatalogueSecurityService
    def executorService
    def assetService


    private static final CONTENT_TYPES = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream', 'application/xml', 'text/xml']
    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]

    protected static getErrors(Map params, MultipartFile file) {
        def errors = []
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
            render status: HttpStatus.UNAUTHORIZED
            return
        }


        if (!(request instanceof MultipartHttpServletRequest)) {
            respond "errors": [message: 'No file selected']
            return
        }

        def errors = [], response

        String conceptualDomainName
        MultipartFile file = request.getFile("file")
        errors.addAll(getErrors(params, file))

        if (errors) {
            response = ["errors": errors]
            respond response
            return
        }
        conceptualDomainName = trimString(params.conceptualDomain)
        def confType = file.getContentType()

        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xls")) {
            def asset = storeAsset(params, file, 'application/vnd.ms-excel')
            def id = asset.id
            InputStream inputStream = file.inputStream
            HeadersMap headersMap = populateHeaders(request.JSON.headersMap ?: [:])
            executorService.submit {
                try {
                    ExcelLoader parser = new ExcelLoader(inputStream)
                    def (headers, rows) = parser.parse()
                    Collection<CatalogueElement> catElements =  dataImportService.importData(headers, rows, headersMap)
                    makeRelationships(catElements, finalizeAsset(id))
                } catch (Exception e) {
                    logError(id, e)
                }
            }
            redirectToAsset(id)
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
                    Asset updated = finalizeAsset(id)
                    classifyAsset(updated, classification)
                } catch (Exception e) {
                    logError(id, e)
                }
            }
            redirectToAsset(id)

            return
        }

        if (file.size > 0 && file.originalFilename.endsWith(".mc")) {
            def asset = storeAsset(params, file, 'application/model-catalogue')
            def id = asset.id
            InputStream inputStream = file.inputStream

            executorService.submit {
                try {
                    Set<CatalogueElement> created = initCatalogueService.importMCFile(inputStream)

                    Asset updated = finalizeAsset(id)
                    Classification classification = created.find { it instanceof Classification } as Classification
                    classifyAsset(updated, classification)
                    for (CatalogueElement element in created) {
                        asset.addToRelatedTo(element)
                    }
                } catch (Exception e) {
                    logError(id, e)
                }
            }

            redirectToAsset(id)
            return
        }

        if (file.size > 0 && file.originalFilename.endsWith(".umlj")) {
            def asset = storeAsset(params, file, 'text/umlj')
            def id = asset.id
            InputStream inputStream = file.inputStream
            String name = params?.name

            executorService.submit {
                try {
                    Classification classification = Classification.findByName(name)
                    if(!classification) classification =  new Classification(name: name).save(flush:true, failOnError:true)
                    umljService.importUmlDiagram(inputStream, name, classification)
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
                    updated.description = "Error importing umlj file: ${e}"
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


        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xsd")) {
            Asset asset = renderImportAsAsset(params, file, conceptualDomainName)
            redirectToAsset(asset.id)
            return
        }

        if (!CONTENT_TYPES.contains(confType)) errors.add("input should be an Excel file but uploaded content is ${confType}")
        if (file.size <= 0) errors.add("The uploaded file is empty")
        response = ["errors": errors]
        respond response
    }

    protected static makeRelationships(Collection<CatalogueElement> catElements, Asset asset){
        catElements.each{
            asset.addToRelatedTo(it)
        }
    }

    protected static classifyAsset(Asset asset, Classification classification){
        if (classification) {
            asset.addToClassifications(classification)
            classification.addToClassifies(asset)
            asset.addToRelatedTo(classification)
        }
    }

    protected static Asset finalizeAsset(Long id){
        Asset updated = Asset.get(id)
        updated.status = ElementStatus.FINALIZED
        updated.description = "Your import has finished."
        updated.save(flush: true, failOnError: true)
        updated
    }
    protected redirectToAsset(Long id){
        webRequest.currentResponse.with {
            def location = grailsApplication.config.grails.serverURL +  "/api/modelCatalogue/core/asset/" + id
            status = 302
            setHeader("Location", location.toString())
            setHeader("X-Asset-ID",  id.toString())
            outputStream.flush()
        }
    }

    protected static logError(Long id,Exception e){
        Asset updated = Asset.get(id)
        updated.refresh()
        updated.status = ElementStatus.FINALIZED
        updated.name = updated.name + " - Error during upload"
        updated.description = "Error importing file: ${e}"
        updated.save(flush: true, failOnError: true)
    }
    protected storeAsset(param, file, contentType = 'application/xslt'){

        String theName = (param.name ?: param.action)

        Asset asset = new Asset(
                name: "Import for " + theName,
                originalFileName: file.originalFilename,
                description: "Your import will be available in this asset soon. Use Refresh action to reload.",
                status: ElementStatus.PENDING,
                contentType: contentType,
                size: 0
        )
        asset.save(flush: true, failOnError: true)
        assetService.storeAssetFromFile(file, asset)
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
