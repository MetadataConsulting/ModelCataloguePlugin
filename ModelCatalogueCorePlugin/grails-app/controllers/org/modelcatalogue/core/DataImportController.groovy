package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.dataarchitect.xsd.XsdLoader
import org.modelcatalogue.core.util.builder.CatalogueBuilder
import org.modelcatalogue.core.xml.CatalogueXmlLoader
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class DataImportController  {

    def dataImportService
    def initCatalogueService
    def XSDImportService
    def OBOService
    def umljService
    def loincImportService
    def modelCatalogueSecurityService
    def executorService
    def elementService
    def classificationService
    def assetService


    private static final CONTENT_TYPES = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream', 'application/xml', 'text/xml']
    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]

    protected static getErrors(Map params, MultipartFile file) {
        def errors = []
        if (file && !params.name) {
            params.name = file.originalFilename
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
            respond("errors": errors)
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

        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xml")) {
            def asset = storeAsset(params, file, 'application/xml')
            def id = asset.id
            InputStream inputStream = file.inputStream
            populateHeaders(request.JSON.headersMap ?: [:])
            executorService.submit {
                try {
                    CatalogueXmlLoader loader = new CatalogueXmlLoader(new CatalogueBuilder(classificationService, elementService))
                    Collection<CatalogueElement> catElements = loader.load(inputStream)
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

        if (file.size > 0 && file.originalFilename.endsWith("c.csv")) {
            def asset = storeAsset(params, file, 'application/model-catalogue')
            def id = asset.id
            InputStream inputStream = file.inputStream

            executorService.submit {
                try {
                    Set<CatalogueElement> created = loincImportService.serviceMethod(inputStream)
                    Asset theAsset = Asset.get(id)
                    for (CatalogueElement element in created) {
                        theAsset.addToRelatedTo(element, skipUniqueChecking: true)
                    }
                    Asset updated = finalizeAsset(id)
                    Classification classification = created.find { it instanceof Classification } as Classification
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
                    Asset theAsset = Asset.get(id)
                    for (CatalogueElement element in created) {
                        theAsset.addToRelatedTo(element, skipUniqueChecking: true)
                    }
                    Asset updated = finalizeAsset(id)
                    Classification classification = created.find { it instanceof Classification } as Classification
                    classifyAsset(updated, classification)
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
                    updated.addToClassifications(classification, skipUniqueChecking: true)
                    classification.addToClassifies(updated, skipUniqueChecking: true)
                    if (classification) {
                        updated.addToRelatedTo(classification, skipUniqueChecking: true)
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

            redirectToAsset(id)
            return
        }


        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xsd")) {
            Asset asset = renderImportAsAsset(params, file, conceptualDomainName)
            redirectToAsset(asset.id)
            return
        }

        if (!CONTENT_TYPES.contains(confType)) errors.add("input should be an Excel file but uploaded content is ${confType}")
        if (file.size <= 0) errors.add("The uploaded file is empty")
        respond "errors": errors
    }

    protected static makeRelationships(Collection<CatalogueElement> catElements, Asset asset){
        catElements.each{
            asset.addToRelatedTo(it, skipUniqueChecking: true)
        }
    }

    protected static classifyAsset(Asset asset, Classification classification){
        if (classification) {
            asset.addToClassifications(classification, skipUniqueChecking: true)
            asset.addToRelatedTo(classification, skipUniqueChecking: true)
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
        response.setHeader("X-Asset-ID",  id.toString())
        redirect url: grailsApplication.config.grails.serverURL +  "/api/modelCatalogue/core/asset/" + id
    }

    protected logError(Long id,Exception e){
        log.error "Error importing Asset[$id]", e
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
                updated.addToRelatedTo(classification, skipUniqueChecking: true)
                updated.addToRelatedTo(conceptualDomain, skipUniqueChecking: true)
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
        headersMap.dataTypeName = params.dataTypeName ?: "Data Type"
        headersMap.dataTypeClassification = params.dataTypeClassification ?: "Data Type Classification"
        headersMap.dataTypeCode = params.dataTypeCode ?: "Data Type Unique Code"
        headersMap.valueDomainName = params.valueDomainName ?: "Value Domain"
        headersMap.valueDomainClassification = params.valueDomainClassification ?: "Value Domain Classification"
        headersMap.valueDomainCode = params.valueDomainCode ?: "Value Domain Unique Code"
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
