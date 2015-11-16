package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.integration.excel.ExcelLoader
import org.modelcatalogue.integration.excel.HeadersMap
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.obo.OboLoader
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class DataImportController  {

    def initCatalogueService
    def umljService
    def loincImportService
    def modelCatalogueSecurityService
    def executorService
    def elementService
    def dataModelService
    def assetService
    def auditService
    def letterAnnotatorService


    private static final CONTENT_TYPES = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream', 'application/xml', 'text/xml']
    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]

    protected static getErrors(Map params, MultipartFile file) {
        def errors = []
        if (file && !params.name) {
            params.name = file.originalFilename
        }
        if (!params?.name) errors.add("no import name")
        if (!file) {
            errors.add("no file")
        } else if (file.size <= 0) {
            errors.add("file is empty")
        }
        return errors
    }

    protected static trimString(string) {
        string.toString().replaceAll('\\[', "").replaceAll('\\]', "").trim()
        return string
    }

    def annotate() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            render status: HttpStatus.UNAUTHORIZED
            return
        }


        if (!(request instanceof MultipartHttpServletRequest)) {
            respond "errors": [message: 'No file selected']
            return
        }

        def errors = []

        MultipartFile file = request.getFile("file")
        errors.addAll(getErrors(params, file))


        Set<Long> dataModels = (params.dataModels ?: '').split(',').collect{ Long.valueOf(it,10) }.toSet()

        if (!dataModels) {
            errors << "no data models"
        }

        if (errors) {
            respond("errors": errors)
            return
        }



        String letter = file.inputStream.text
        def id = assetService.storeReportAsAsset(
                name: params.name,
                originalFileName: params.name.endsWith('.html') ? params.name : "${params.name}.annotated.html",
                contentType: "text/html",
                description: "Your annotated letter will be available soon. Use Refresh action to reload the screen."
        )  { OutputStream out ->
            letterAnnotatorService.annotateLetter(dataModels.collect{ DataModel.get(it)}.toSet(), letter, out)
        }
        redirectToAsset(id)
    }

    def upload() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            render status: HttpStatus.UNAUTHORIZED
            return
        }


        if (!(request instanceof MultipartHttpServletRequest)) {
            respond "errors": [message: 'No file selected']
            return
        }

        def errors = []

        MultipartFile file = request.getFile("file")
        errors.addAll(getErrors(params, file))

        if (errors) {
            respond("errors": errors)
            return
        }
        def confType = file.getContentType()
        boolean isAdmin = modelCatalogueSecurityService.hasRole('ADMIN')

        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xls")) {
            def asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
            def id = asset.id
            InputStream inputStream = file.inputStream
            HeadersMap headersMap = HeadersMap.create(request.JSON.headersMap ?: [:])
            executeInBackground(id, "Imported from Excel") {
                try {
                    DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService, isAdmin)
                    ExcelLoader parser = new ExcelLoader(builder)
                    parser.importData(headersMap, inputStream)
                    finalizeAsset(id)
                } catch (Exception e) {
                    logError(id, e)
                }
            }
            redirectToAsset(id)
            return
        }

        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xml")) {
            def asset = assetService.storeAsset(params, file, 'application/xml')
            def id = asset.id
            InputStream inputStream = file.inputStream
            executeInBackground(id, "Imported from XML") {
                try {
                    CatalogueXmlLoader loader = new CatalogueXmlLoader(new DefaultCatalogueBuilder(dataModelService, elementService, isAdmin))
                    loader.load(inputStream)
                    finalizeAsset(id)
                } catch (Exception e) {
                    logError(id, e)
                }
            }
            redirectToAsset(id)
            return
        }

        if (file.size > 0 && file.originalFilename.endsWith(".obo")) {
            def asset = assetService.storeAsset(params, file, 'text/obo')
            def id = asset.id
            InputStream inputStream = file.inputStream
            String name = params?.name
            String idpattern = params.idpattern
            executeInBackground(id, "Imported from OBO") {
                try {
                    DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService, isAdmin)
                    OboLoader loader = new OboLoader(builder)
                    idpattern = idpattern ?: "${grailsApplication.config.grails.serverURL}/catalogue/ext/${OboLoader.OBO_ID}/:id".toString().replace(':id', '$id')
                    loader.load(inputStream, name, idpattern)
                    DataModel dataModel = builder.created.find { it.instanceOf(DataModel) } as DataModel
                    Asset updated = finalizeAsset(id)
                    assignAssetToModel(updated, dataModel)
                } catch (Exception e) {
                    logError(id, e)
                }
            }
            redirectToAsset(id)

            return
        }

        if (file.size > 0 && file.originalFilename.endsWith("c.csv")) {
            def asset = assetService.storeAsset(params, file, 'application/model-catalogue')
            def id = asset.id
            InputStream inputStream = file.inputStream

            executeInBackground(id, "Imported from LOINC")  {
                try {
                    loincImportService.serviceMethod(inputStream)
                    finalizeAsset(id)
                } catch (Exception e) {
                    logError(id, e)
                }
            }

            redirectToAsset(id)
            return
        }

        if (file.size > 0 && file.originalFilename.endsWith(".mc")) {
            def asset = assetService.storeAsset(params, file, 'application/model-catalogue')
            def id = asset.id
            InputStream inputStream = file.inputStream

            executeInBackground(id, "Imported from Model Catalogue DSL")  {
                try {
                    Set<CatalogueElement> created = initCatalogueService.importMCFile(inputStream)
                    Asset updated = finalizeAsset(id)
                    DataModel dataModel = created.find { it instanceof DataModel } as DataModel
                    assignAssetToModel(updated, dataModel)
                } catch (Exception e) {
                    logError(id, e)
                }
            }

            redirectToAsset(id)
            return
        }

        if (file.size > 0 && file.originalFilename.endsWith(".umlj")) {
            def asset = assetService.storeAsset(params, file, 'text/umlj')
            def id = asset.id
            InputStream inputStream = file.inputStream
            String name = params?.name

            executeInBackground(id, "Imported from Style UML")  {
                try {
                    DataModel dataModel = DataModel.findByName(name)
                    if(!dataModel) dataModel =  new DataModel(name: name).save(flush:true, failOnError:true)
                    umljService.importUmlDiagram(inputStream, name, dataModel)
                    Asset updated = Asset.get(id)
                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your import has finished."
                    updated.save(flush: true, failOnError: true)
                    updated.addToDeclaredWithin(dataModel, skipUniqueChecking: true)
                    dataModel.addToDeclares(updated, skipUniqueChecking: true)
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
        respond "errors": errors
    }

    protected static assignAssetToModel(Asset asset, DataModel dataModel){
        if (dataModel) {
            asset.addToDeclaredWithin(dataModel, skipUniqueChecking: true)
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


    protected executeInBackground(Long assetId, String message, Closure code) {
        Long userId = modelCatalogueSecurityService.currentUser?.id
        executorService.submit {
            auditService.logExternalChange(Asset.get(assetId), userId, message, code)
        }
    }
}
