package org.modelcatalogue.core

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataimport.excel.ConfigStatelessExcelLoader
import org.modelcatalogue.core.dataimport.excel.ExcelImportType
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.dataimport.excel.HeadersMap
import org.modelcatalogue.core.dataimport.excel.ConfigExcelLoader
import org.modelcatalogue.core.dataimport.excel.nt.uclh.OpenEhrExcelLoader
import org.modelcatalogue.core.dataimport.excel.nt.uclh.UCLHExcelLoader
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.obo.OboLoader
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.springframework.scheduling.annotation.Async
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DataImportController  {

    def initCatalogueService
    def modelCatalogueSecurityService
    def executorService
    def elementService
    def dataModelService
    def assetService
    def auditService
    def dataClassService
    DataImportXmlService dataImportXmlService
    DataImportOboService dataImportOboService
    AssetGormService assetGormService
    UserGormService userGormService

    private static final List<String> CONTENT_TYPES = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream', 'application/xml', 'text/xml', 'application/zip']
    static responseFormats = ['json']
    static allowedMethods = [upload: "POST", excelImportTypesHumanReadable: 'GET']

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
    def excelImportTypesHumanReadable() {
        Map<String, List<String>> result = ['excelImportTypes': ExcelImportType.humanReadableNames]
        render result as JSON
    }
    def upload() {
        // called from importCtrl.coffee which is the AngularJS controller for ALL import dialogues.
        // having just one upload action (and on the front-end, one importCtrl) is a bit hairy but there we go...


        if (!(request instanceof MultipartHttpServletRequest)) {
            respond "errors": [message: 'No file selected']
            return
        }

        //// get stuff from the request.
        // user-provided model name. It's the filename by default. In the past we have just gotten the filename directly anyways. But it's here.
        String modelName = request.getParameter('modelName')


        // XML config (including headers map) file for any "generic/customizable" excel importer. At some point, we might want to introduce some validation, either on front or back end or both. If both, the XSD file used to validate should be the same, and provided by the backend. You can access file.inputStream.
        MultipartFile excelConfigXMLFileMultipart = request.getFile("excelConfigXMLFile")

        ExcelImportType excelImportType = ExcelImportType.fromHumanReadableName(request.getParameter('excelImportType'))


        // the actual file to be imported.
        MultipartFile file = request.getFile("file") // may be excel, Obo, etc. etc.

        List<String> errors = getErrors(params, file)
        if (errors) {
            respond("errors": errors)
            return
        }

        String confType = file.getContentType()

        boolean isAdmin = SpringSecurityUtils.ifAnyGranted(MetadataRolesUtils.getRolesFromAuthority('ADMIN').join(','))
        DefaultCatalogueBuilder defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService, isAdmin)

        Long userId = modelCatalogueSecurityService.currentUser?.id

        // Customizable Excel Loader based on the LoincExcelLoader that can take a headersMap
        if (excelImportType == ExcelImportType.LOINC ||
            excelImportType == ExcelImportType.GOSH_LAB_TEST_CODES) {
            if (checkFileNameTypeAndContainsString(file,'.xls')) {
                Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
                Long id = asset.id
                InputStream inputStream = file.inputStream
                InputStream xmlConfigStream = excelConfigXMLFileMultipart.inputStream
                String filename = file.originalFilename
                Workbook wb = WorkbookFactory.create(inputStream)
                defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
                executeInBackground(id, "Imported from Excel") {
                    // TODO: Use the excelConfigXMLFile in a method similar to that below.
                    loadConfigSpreadsheet(wb, modelName, xmlConfigStream, id, userId)

                }
                redirectToAsset(id)
                return
            }
        }
        String suffix = ""
        if (excelImportType == ExcelImportType.REIMPORT_FROM_EXCEL_EXPORTER) {
            // "General Excel file"-- "THE MC Excel file" -- actually the format produced by ExcelExporter that has parent data class etc.
            suffix = "mc.xls"
            if (checkFileNameTypeAndContainsString(file,suffix)) {
                Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
                Long id = asset.id
                InputStream inputStream = file.inputStream
                String filename = file.originalFilename
                Workbook wb = WorkbookFactory.create(inputStream)
                defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
                executeInBackground(id, "Imported from Excel") {
                    loadMCSpreadsheet(wb, filename, defaultCatalogueBuilder, id, userId)
                }
                redirectToAsset(id)
                return
            }
        }

        if (excelImportType == ExcelImportType.NORTH_THAMES_DATA_SOURCE_MAPPING) {

            //North Thames GMC specific import type for cancer data
            suffix = "ca_nt_rawimport.xls"
            if (checkFileNameTypeAndContainsString(file,suffix)) {
                Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
                Long id = asset.id
                InputStream inputStream = file.inputStream
                String filename = file.originalFilename
                Workbook wb = WorkbookFactory.create(inputStream)
                defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
                executeInBackground(id, "Imported from Excel") {
                    loadNTSpreadsheet( wb, filename,defaultCatalogueBuilder, suffix, id, userId)
                }
                redirectToAsset(id)
                return
            }

            //North Thames GMC specific import type for rare disease data
            suffix = "rd_nt_rawimport.xls"
            if (checkFileNameTypeAndContainsString(file,suffix)) {
                Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
                Long id = asset.id
                InputStream inputStream = file.inputStream
                String filename = file.originalFilename
                Workbook wb = WorkbookFactory.create(inputStream)
                defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
                executeInBackground(id, "Imported from Excel") {
                    loadNTSpreadsheet( wb, filename,defaultCatalogueBuilder, suffix, id, userId)
                }
                redirectToAsset(id)
                return
            }
        }

        // openEHR
        suffix = "openEHR.xls"
        if (checkFileNameTypeAndContainsString(file,suffix)) {
            Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
            Long id = asset.id
            InputStream inputStream = file.inputStream
            String filename = file.originalFilename
            Workbook wb = WorkbookFactory.create(inputStream)
            defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
            executeInBackground(id, "Imported from Excel") {
                loadOpenEhrSpreadsheet( wb, filename,defaultCatalogueBuilder, suffix, id, userId)
            }
            finalizeAsset(id, (DataModel) (defaultCatalogueBuilder.created.find {it.instanceOf(DataModel)} ?: defaultCatalogueBuilder.created.find{it.dataModel}?.dataModel), userId)
            redirectToAsset(id)
            return
        }

        if (excelImportType == ExcelImportType.STANDARD) {
            //Default excel import - "standardImport" – which assumes data is in the 'Grid data' format
            suffix = "xls"
            if (checkFileNameTypeAndContainsString(file,suffix)) {
                Asset asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
                Long id = asset.id
                InputStream inputStream = file.inputStream
                Workbook wb = WorkbookFactory.create(inputStream)
                defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", id)
                executeInBackground(id, "Imported from Excel") {
                    try {
                        ExcelLoader parser = new ExcelLoader()
                        parser.buildModelFromStandardWorkbookSheet(HeadersMap.createForStandardExcelLoader(), inputStream, )
                        finalizeAsset(id, (DataModel) (defaultCatalogueBuilder.created.find {it.instanceOf(DataModel)} ?: defaultCatalogueBuilder.created.find{it.dataModel}?.dataModel), userId)
                    } catch (Exception e) {
                        logError(id, e)
                    }
                }
                redirectToAsset(id)
                return
            }
        }


        if (checkFileNameTypeAndContainsString(file, '.zip')) {
            Asset asset = assetService.storeAsset(params, file, 'application/zip')
            Long id = asset.id
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

        if (checkFileNameTypeAndContainsString(file, '.xml')) {
            Long assetId = dataImportXmlService.importFile(params, file)
            redirectToAsset(assetId)
            return
        }

        if (checkFileNameEndsWith(file, '.obo')) {
            Long assetId = dataImportOboService.importFile(params, file)
            redirectToAsset(assetId)
            return
        }

        if (checkFileNameEndsWith(file, '.csv')) {
            Asset asset = assetService.storeAsset(params, file, 'application/model-catalogue')
            Long id = asset.id
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

        if (checkFileNameEndsWith(file, '.mc')) {
            Asset asset = assetService.storeAsset(params, file, 'application/model-catalogue')
            Long id = asset.id
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


        if (!CONTENT_TYPES.contains(confType)) errors.add("input should be an Excel, XML, MC, OBO, UML or LOINC file but uploaded content is ${confType}")
        respond "errors": errors
    }
    protected static boolean checkFileNameEndsWith(MultipartFile file, String suffix) {

        file.size > 0 && file.originalFilename.endsWith(suffix)
    }
    protected static boolean checkFileNameTypeAndContainsString(MultipartFile file, String suffix) {
        CONTENT_TYPES.contains(file.getContentType()) &&
            file.size > 0 &&
            file.originalFilename.contains(suffix)
    }

    protected Asset finalizeAsset(Long id, DataModel dataModel, Long userId){
        BuildProgressMonitor.get(id)?.onCompleted()

        Asset assetInstance = assetGormService.finalizeAsset(id, dataModel, userId)

        if ( userId && userGormService.exists(userId) ) {
            User userInstance = userGormService.findById(userId)
            userInstance.createLinkTo(dataModel, RelationshipType.favouriteType)
        }

        assetInstance
    }

    protected redirectToAsset(Long id){
        response.setHeader("X-Asset-ID",  id.toString())
        redirect url: grailsApplication.config.grails.serverURL +  "/api/modelCatalogue/core/asset/" + id
    }

    protected logError(Long id,Exception e){
        BuildProgressMonitor.get(id)?.onError(e)
        log.error "Error importing Asset[$id]", e
        assetGormService.finalizeAssetWithError(e)
    }

    //simply halts if the closure includes a file stream object
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

    @Async
    protected void loadNTSpreadsheet(Workbook wb){
        auditService.betterMute {
            try {
                ExcelLoader loader = new ExcelLoader()
                loader.updateFromWorkbookSheet(wb, 0)

            } catch (Exception e) {
                logError(e)
            }
        }
    }

    @Async
    protected void loadNTSpreadsheet(Workbook wb, String filename, DefaultCatalogueBuilder defaultCatalogueBuilder, String suffix, Long id, Long userId){
        Pair<String,String> modelDetails = getModelDetails(suffix)
        auditService.betterMute {
            try{
                UCLHExcelLoader loader = new UCLHExcelLoader(false)
                String dataOwnerAndGelModel = ExcelLoader.getOwnerAndGelModelFromFileName(filename, '_nt_rawimport')
                List<String> modelNames = loader.loadModel(wb,modelDetails.right,dataOwnerAndGelModel)
                DataModel referenceModel = DataModel.findByNameAndStatus(modelDetails.left, ElementStatus.FINALIZED)
                loader.addRelationshipsToModels(referenceModel, modelNames)
                finalizeAsset(id, (DataModel) (defaultCatalogueBuilder.created.find {it.instanceOf(DataModel)} ?: defaultCatalogueBuilder.created.find{it.dataModel}?.dataModel), userId)

            } catch (Exception e) {
                logError(id, e)
            }
        }
    }
    // the generic loader based on the LOINC loader
    @Async
    protected void loadConfigSpreadsheet(Workbook wb, String modelName, InputStream xmlConfigStream, Long id, Long userId) {
        auditService.betterMute {
            try {
                ConfigExcelLoader loader = new ConfigExcelLoader(modelName, xmlConfigStream)
//                ConfigStatelessExcelLoader loader = new ConfigStatelessExcelLoader(modelName, xmlConfigStream)
                loader.buildModel(wb)
                finalizeAsset(id, (DataModel) (DataModel.findByName(modelName)), userId)
            }
            catch (Exception e) {
                logError(id, e)
            }
        }
    }

    @Async
    protected void loadMCSpreadsheet(Workbook wb, String filename, DefaultCatalogueBuilder defaultCatalogueBuilder, Long id, Long userId) {
        auditService.betterMute {
            try {
                ExcelLoader loader = new ExcelLoader()
                loader.buildModelFromSpreadsheetFromExcelExporter(
                        HeadersMap.createForSpreadsheetFromExcelExporter(),
                        wb,
                        0,
                        defaultCatalogueBuilder,
                        filename.split(/\.mc\.xls/)[0]
                )
                finalizeAsset(id, (DataModel) (defaultCatalogueBuilder.created.find {it.instanceOf(DataModel)} ?: defaultCatalogueBuilder.created.find{it.dataModel}?.dataModel), userId)

            }
            catch (Exception e) {
                logError(id, e)
            }
        }
    }


    @Async
    protected void loadOpenEhrSpreadsheet(Workbook wb, String filename,DefaultCatalogueBuilder defaultCatalogueBuilder, String suffix, Long id, Long userId){
        executorService.submit {
            auditService.betterMute {
                try {
                    OpenEhrExcelLoader loader = new OpenEhrExcelLoader()
                    DataModel dm = loader.loadModel(wb)
                    finalizeAsset(id, dm, userId)

                } catch (Exception e) {
                    logError(id, e)
                }
            }
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
