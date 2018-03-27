package org.modelcatalogue.core.genomics

import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class RareDiseaseImportController {

    def grailsApplication
    def assetService
    def auditService
    def elementService
    def executorService
    def dataModelService
    def rareDiseaseImportService
    def modelCatalogueSecurityService
    DataModelAclService dataModelAclService

    private static final CONTENT_TYPES = ['text/csv']
    static responseFormats = ['json']
    static allowedMethods = [upload: "POST"]

    def upload() {

        if (!(request instanceof MultipartHttpServletRequest)) {
            respond "errors": [message: 'No file selected']
            return
        }

        MultipartFile file = request.getFile("file")
        if (!file) {
            respond("errors": ["no file found"])
            return
        } else if (file.size <= 0) {
            respond("errors": ["file is empty"])
            return
        }

        boolean canCreateRelationshipTypes = dataModelAclService.isAdminOrHasAdministratorPermission(getDataModel())
        def builder = new DefaultCatalogueBuilder(dataModelService, elementService, canCreateRelationshipTypes)

        if (CONTENT_TYPES.contains(file.contentType) && file.originalFilename.contains(".csv")) {
            def asset = assetService.storeAsset(params, file, 'application/vnd.ms-excel')
            builder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", asset.id)

            def dataModel = elementService.findByModelCatalogueId(DataModel, params.dataModelId)
            def hpoDataModel = elementService.findByModelCatalogueId(DataModel, params.hpoDataModelId)
            def testDataModel = elementService.findByModelCatalogueId(DataModel, params.testDataModelId)
            executeInBackground(asset.id, "Imported from Rare Disease Csv") {
                try {
                    def created = rareDiseaseImportService.importDisorderedCsv(builder, dataModel, hpoDataModel,
                        testDataModel, file.inputStream)
                    finalizeAsset(asset.id, (DataModel) (created.find {it.instanceOf(DataModel)} ?: created.find{it.dataModel}?.dataModel),
                        modelCatalogueSecurityService.currentUser?.id)
                } catch (Exception e) {
                    logError(asset.id, e)
                }
            }

            // redirect to asset
            response.setHeader("X-Asset-ID", asset.id.toString())
            redirect url: grailsApplication.config.grails.serverURL + "/api/modelCatalogue/core/asset/" + asset.id
            return
        }

        respond("errors": ["usupported content type ${file.contentType}, use some of ${CONTENT_TYPES}"])
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

    protected logError(Long id, Exception e) {
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

    protected DataModel getDataModel(){
        DataModel dataModel
        if(resource!=DataModel){
            dataModel = (resource.get(params.id)?.dataModel)
        }else{
            dataModel = (resource.get(params.id))
        }
        dataModel
    }
}
