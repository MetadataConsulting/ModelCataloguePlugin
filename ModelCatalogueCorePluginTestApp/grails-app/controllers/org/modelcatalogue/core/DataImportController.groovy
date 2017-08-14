package org.modelcatalogue.core

import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.dataimport.excel.HeadersMap
import org.modelcatalogue.core.dataimport.excel.TextLoader
import org.modelcatalogue.core.dataimport.excel.uclh.UCLHTestLoader
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.dataimport.excel.uclh.UCLHExcelLoader
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


    private static final List<String> CONTENT_TYPES = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream', 'application/xml', 'text/xml', 'application/zip']
    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]

    protected static List<String> getErrors(Map params, MultipartFile file) {
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

        MultipartFile file = request.getFile("file")

        List<String> errors = getErrors(params, file)
        if (errors) {
            respond("errors": errors)
            return
        }

        String confType = file.getContentType()

        boolean isAdmin = modelCatalogueSecurityService.hasRole('ADMIN')
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService, isAdmin)

        Long userId = modelCatalogueSecurityService.currentUser?.id



        if (checkFileNameContainsAndType(file,"nt_rawimport.xls")) {
            def asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
            def id = asset.id
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream

                    String test1 = "TEST MESSAGE"
                    println test1
                TextLoader tl = new TextLoader()
                UCLHTestLoader uclh = new UCLHTestLoader()
                    ExcelLoader ldr1 = new ExcelLoader()
                    UCLHExcelLoader loader1 = new UCLHExcelLoader(false)


            executeInBackground(id, "Imported from Excel") {
                try {
                    String test = "TEST MESSAGE"
                    println test

                    ExcelLoader ldr = new ExcelLoader()
                    UCLHExcelLoader loader = new UCLHExcelLoader(false)

                    //UCLHExcelLoader parser = new UCLHExcelLoader(false) //test=true randomizing model names
                    Pair<String, List<String>> xmlAndDataModelNames = loader.buildXmlFromWorkbookSheet(WorkbookFactory.create(inputStream), 0, ExcelLoader.getOwnerFromFileName(file.originalFilename, '_nt_rawimport'))
                    parser.addRelationshipsToModels('Cancer Model', xmlAndDataModelNames.right)
                    finalizeAsset(id, (DataModel) (builder.created.find {it.instanceOf(DataModel)} ?: builder.created.find{it.dataModel}?.dataModel), userId)
                } catch (Exception e) {
                    logError(id, e)
                }
            }
            redirectToAsset(id)
            return
        }

        if (checkFileNameContainsAndType(file, '.xls')) {
            Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
            def id = asset.id
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            HeadersMap headersMap = HeadersMap.createForStandardExcelLoader(request.JSON.headersMap ?: [:])
            executeInBackground(id, "Imported from Excel") {
                try {
//                    ExcelLoader parser = new ExcelLoader(builder)
//                    parser.buildXmlFromWorkbook(headersMap, WorkbookFactory.create(inputStream))
                    UCLHExcelLoader parser = new UCLHExcelLoader(false) //test=true randomizing model names
                    Pair<String, List<String>> xmlAndDataModelNames = parser.buildXmlFromWorkbookSheet(WorkbookFactory.create(inputStream))
                    parser.addRelationshipsToModels('Cancer Model', xmlAndDataModelNames.right)
                    finalizeAsset(id, (DataModel) (builder.created.find {it.instanceOf(DataModel)} ?: builder.created.find{it.dataModel}?.dataModel), userId)
                } catch (Exception e) {
                    logError(id, e)
                }
            }
            redirectToAsset(id)
            return
        }

        if (checkFileNameContainsAndType(file, '.zip')) {
            Asset asset = assetService.storeAsset(params, file, 'application/zip')
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

        if (checkFileNameContainsAndType(file, '.xml')) {
            Asset asset = assetService.storeAsset(params, file, 'application/xml')
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

        if (checkFileNameEndsWith('.obo')) {
            Asset asset = assetService.storeAsset(params, file, 'text/obo')
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

        if (checkFileNameEndsWith('c.csv')) {
            Asset asset = assetService.storeAsset(params, file, 'application/model-catalogue')
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

        if (checkFileNameEndsWith('.mc')) {
            Asset asset = assetService.storeAsset(params, file, 'application/model-catalogue')
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

        if (checkFileNameEndsWith('.umlj')) {
            Asset asset = assetService.storeAsset(params, file, 'text/umlj')
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
    protected static boolean checkFileNameEndsWith(MultipartFile file, String suffix) {

        file.size > 0 && file.originalFilename.endsWith(suffix)
    }
    protected static boolean checkFileNameContainsAndType(MultipartFile file, String suffix) {
        CONTENT_TYPES.contains(file.getContentType()) &&
            file.size > 0 &&
            file.originalFilename.contains(suffix)
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

    protected String getOwnerFromFileName(String sampleFile){
        sampleFile.find(/(.*)_nt_rawimport.*/){match,firstcapture ->
            firstcapture
        }
    }
}
