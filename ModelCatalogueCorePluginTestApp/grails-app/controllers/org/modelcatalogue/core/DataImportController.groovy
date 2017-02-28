package org.modelcatalogue.core

import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.integration.excel.ExcelLoader
import org.modelcatalogue.integration.excel.HeadersMap
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.obo.OboLoader
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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


    private static final CONTENT_TYPES = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream', 'application/xml', 'text/xml', 'application/zip']
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

        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService, isAdmin)

        Long userId = modelCatalogueSecurityService.currentUser?.id

        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".xls")) {
            def asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
            def id = asset.id
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            HeadersMap headersMap = HeadersMap.create(request.JSON.headersMap ?: [:])
            executeInBackground(id, "Imported from Excel") {
                try {
                    ExcelLoader parser = new ExcelLoader(builder)
                    parser.importData(headersMap, inputStream)
                    finalizeAsset(id, (DataModel) (builder.created.find {it.instanceOf(DataModel)} ?: builder.created.find{it.dataModel}?.dataModel), userId)
                } catch (Exception e) {
                    logError(id, e)
                }
            }
            redirectToAsset(id)
            return
        }

        if (CONTENT_TYPES.contains(confType) && file.size > 0 && file.originalFilename.contains(".zip")) {
            def asset = assetService.storeAsset(params, file, 'application/zip')
            def id = asset.id
            builder.monitor = BuildProgressMonitor.create("Importing archive $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            executeInBackground(id, "Imported from XML ZIP") {
                try {
                    ZipInputStream zis = new ZipInputStream(inputStream)
                    ZipEntry entry = zis.nextEntry
                    while (entry) {
                        if (!entry.directory && entry.name.endsWith('.xml')) {
                            builder.monitor.onNext("Parsing $entry.name")
                            CatalogueXmlLoader loader = new CatalogueXmlLoader(builder)
                            loader.load(POIFSFileSystem.createNonClosingInputStream(zis))
                        }

                        entry = zis.nextEntry
                    }
                    finalizeAsset(id, (DataModel) (builder.created.find {it.instanceOf(DataModel)} ?: builder.created.find{it.dataModel}?.dataModel), userId)
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
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            executeInBackground(id, "Imported from XML") {
                try {
                    CatalogueXmlLoader loader = new CatalogueXmlLoader(builder)
                    loader.load(inputStream)
                    finalizeAsset(id, (DataModel) (builder.created.find {it.instanceOf(DataModel)} ?: builder.created.find{it.dataModel}?.dataModel), userId)
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
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            String name = params?.name
            executeInBackground(id, "Imported from OBO") {
                try {
                    OboLoader loader = new OboLoader(builder)
                    loader.load(inputStream, name)
                    finalizeAsset(id, (DataModel) (builder.created.find {it.instanceOf(DataModel)} ?: builder.created.find{it.dataModel}?.dataModel), userId)
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
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream

            executeInBackground(id, "Imported from LOINC")  {
                try {
                    Set<CatalogueElement> created = loincImportService.serviceMethod(inputStream)
                    finalizeAsset(id, (DataModel) (created.find {it.instanceOf(DataModel)} ?: created.find{it.dataModel}?.dataModel), userId)
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
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream

            executeInBackground(id, "Imported from Model Catalogue DSL")  {
                try {
                    Set<CatalogueElement> created = initCatalogueService.importMCFile(inputStream, false, builder)
                    finalizeAsset(id, (DataModel) (created.find {it.instanceOf(DataModel)} ?: created.find{it.dataModel}?.dataModel), userId)
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
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            String name = params?.name

            executeInBackground(id, "Imported from Style UML")  {
                try {
                    DataModel dataModel = DataModel.findByName(name)
                    if(!dataModel) dataModel =  new DataModel(name: name).save(flush:true, failOnError:true)
                    umljService.importUmlDiagram(builder, inputStream, name, dataModel)
                    Asset updated = Asset.get(id)
                    updated.dataModel = dataModel
                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your import has finished."
                    updated.dataModel = dataModel
                    updated.save(flush: true, failOnError: true)
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

        if (!CONTENT_TYPES.contains(confType)) errors.add("input should be an Excel, XML, MC, OBO, UML or LOINC file but uploaded content is ${confType}")
        respond "errors": errors
    }

    protected static Asset finalizeAsset(Long id, DataModel dataModel, Long userId){
        BuildProgressMonitor.get(id)?.onCompleted()

        Asset updated = Asset.get(id)

        if (!dataModel) {
            return updated
        }
        updated.dataModel = dataModel
        updated.status = ElementStatus.FINALIZED
        updated.description = "Your import has finished."
        updated.save(flush: true, failOnError: true)

        if (userId && User.exists(userId)) {
            User.get(userId).createLinkTo(dataModel, RelationshipType.favouriteType)
        }

        updated
    }
    protected redirectToAsset(Long id){
        response.setHeader("X-Asset-ID",  id.toString())
        redirect url: grailsApplication.config.grails.serverURL +  "/api/modelCatalogue/core/asset/" + id
    }

    protected logError(Long id,Exception e){
        BuildProgressMonitor.get(id)?.onError(e)
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
