package org.modelcatalogue.core

import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.dataimport.excel.HeadersMap
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.dataimport.excel.nt.uclh.UCLHExcelLoader
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
        if (!modelCatalogueSecurityService.hasRole('SUPERVISOR')) {
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
        DefaultCatalogueBuilder defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService, isAdmin)

        Long userId = modelCatalogueSecurityService.currentUser?.id


        String suffix = "ca_nt_rawimport.xls"
        if (checkFileNameContainsAndType(file,suffix)) {
            Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
            def id = asset.id
            InputStream inputStream = file.inputStream
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            executeInBackground(id, "Imported from Excel") {
                loadSpreadsheet( file, defaultCatalogueBuilder, suffix, id, userId)
            }
            redirectToAsset(id)
            return
        }

        suffix = "rd_nt_rawimport.xls"
        if (checkFileNameContainsAndType(file,suffix)) {
            Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
            def id = asset.id
            InputStream inputStream = file.inputStream
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            executeInBackground(id, "Imported from Excel") {
                loadSpreadsheet( file, defaultCatalogueBuilder, suffix, id, userId)
            }
            redirectToAsset(id)
            return
        }



        if (checkFileNameContainsAndType(file, '.zip')) {
            Asset asset = assetService.storeAsset(params, file, 'application/zip')
            def id = asset.id
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing archive $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            executeInBackground(id, "Imported from XML ZIP") {
                try {
                    ZipInputStream zis = new ZipInputStream(inputStream)
                    ZipEntry entry = zis.nextEntry
                    while (entry) {
                        if (!entry.directory && entry.name.endsWith('.xml')) {
                            defaultCatalogueBuilder.monitor.onNext("Parsing $entry.name")
                            CatalogueXmlLoader loader = new CatalogueXmlLoader(defaultCatalogueBuilder)
                            loader.load(POIFSFileSystem.createNonClosingInputStream(zis))
                        }

                        entry = zis.nextEntry
                    }
                    finalizeAsset(id, (DataModel) (defaultCatalogueBuilder.created.find {it.instanceOf(DataModel)} ?: defaultCatalogueBuilder.created.find{it.dataModel}?.dataModel), userId)
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
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            executeInBackground(id, "Imported from XML") {
                try {
                    CatalogueXmlLoader loader = new CatalogueXmlLoader(defaultCatalogueBuilder)
                    loader.load(inputStream)
                    finalizeAsset(id, (DataModel) (defaultCatalogueBuilder.created.find {it.instanceOf(DataModel)} ?: defaultCatalogueBuilder.created.find{it.dataModel}?.dataModel), userId)
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
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            String name = params?.name
            executeInBackground(id, "Imported from OBO") {
                try {
                    OboLoader loader = new OboLoader(defaultCatalogueBuilder)
                    loader.load(inputStream, name)
                    finalizeAsset(id, (DataModel) (defaultCatalogueBuilder.created.find {it.instanceOf(DataModel)} ?: defaultCatalogueBuilder.created.find{it.dataModel}?.dataModel), userId)
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
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
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
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream

            executeInBackground(id, "Imported from Model Catalogue DSL")  {
                try {
                    Set<CatalogueElement> created = initCatalogueService.importMCFile(inputStream, false, defaultCatalogueBuilder)
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
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            InputStream inputStream = file.inputStream
            String name = params?.name
            executeInBackground(id, "Imported from Style UML")  {
                try {
                    DataModel dataModel = DataModel.findByName(name)
                    if(!dataModel) dataModel =  new DataModel(name: name).save(flush:true, failOnError:true)
                    umljService.importUmlDiagram(defaultCatalogueBuilder, inputStream, name, dataModel)
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

    protected void loadSpreadsheet(MultipartFile file, DefaultCatalogueBuilder defaultCatalogueBuilder, String suffix, Long id, Long userId){
        Pair<String,String> modelDetails = getModelDetails(suffix)
        InputStream inputStream = file.inputStream

        try{
            UCLHExcelLoader loader = new UCLHExcelLoader(false)
            String dataOwner = ExcelLoader.getOwnerFromFileName(file.originalFilename, '_nt_rawimport')
            Workbook wb = WorkbookFactory.create(inputStream)
            Pair<Closure, List<String>> instructionsAndDataModelNames = loader.buildInstructionsAndModelNamesFromWorkbookSheet(wb, modelDetails.right, dataOwner)
            loader.buildModelFromInstructions(defaultCatalogueBuilder, instructionsAndDataModelNames.left)
            DataModel cancerModel = DataModel.findByName(modelDetails.left)
            loader.addRelationshipsToModels(cancerModel, instructionsAndDataModelNames.right)
            finalizeAsset(id, (DataModel) (defaultCatalogueBuilder.created.find {it.instanceOf(DataModel)} ?: defaultCatalogueBuilder.created.find{it.dataModel}?.dataModel), userId)
            } catch (Exception e) {
                logError(id, e)
            }
    }
    /*
    *       getModelDetails
    *       Details could be put into configuration file - perhaps excel configuration?
    */
    protected Pair<String,String> getModelDetails(String suffix){
        String modelName = ""
        String sheetName = ""
        if(suffix.split(/_/)[0].equals('rd')){
            modelName = "Rare Diseases"
            sheetName = "RD-data-items"
        }else{
            modelName = "Cancer Model"
            sheetName = "Ca-Data-items"
        }
        return Pair.of(modelName, sheetName)
    }


}
