package org.modelcatalogue.core.gel

import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.publishing.changelog.ChangelogGenerator

import java.util.concurrent.ExecutorService

import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.api.ElementStatus;
import org.modelcatalogue.core.audit.AuditService
/**
 * Various reports generations as an asset. 
 *  
 *
 */
class ClassificationReportsController {

    ExecutorService executorService
    AuditService auditService
    AssetService assetService
    SecurityService modelCatalogueSecurityService
    DataClassService dataClassService
    
    def index() { }
    
    def gereportDoc() {
        DataModel classification = DataModel.get(params.id)
        def models = getDataClassesForDataModels(params.id as Long)
        

        def assetId=storeAssetAsDocx(classification)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    private def storeAssetAsDocx(DataModel classification){
        Asset asset = new Asset(
                name: "$classification.name report as MS Word Document",
                originalFileName: "${classification.name}-${classification.status}-${classification.version}.docx",
                description: "Your classification report  will be available in this asset soon. Use Refresh action to reload",
                status: ElementStatus.PENDING,
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                size: 0
                )

        asset.save(flush: true, failOnError: true)

        Long id = asset.id
        Long authorId = modelCatalogueSecurityService.currentUser?.id
        Long classificationId = classification.id

        executorService.submit {
            auditService.withDefaultAuthorId(authorId) {
                Asset updated = Asset.get(id)
                try {
                    //do the hard work
                    assetService.storeAssetWithSteam(updated, "application/vnd.openxmlformats-officedocument.wordprocessingml.document",) { OutputStream out ->
                        new DataModelToDocxExporter(DataModel.get(classificationId)).export(out)
                    }

                   

                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your data model is ready. Use Download button to download it."
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} with id=${id}", e

                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = "Error generating data model report" +":$e"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }
        return asset.id;
    }

    def changelogDoc() {
        DataClass dataClass = DataClass.get(params.id)

        def assetId = storeChangelogAsset(dataClass)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    private def long storeChangelogAsset(DataClass model){
        Asset asset = new Asset(
                name: "$model.name changelog as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}-changelog.docx",
                description: "Your classification report will be available in this asset soon. Use Refresh action to reload",
                status: ElementStatus.PENDING,
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                size: 0
        )

        asset.save(flush: true, failOnError: true)

        Long id = asset.id
        Long authorId = modelCatalogueSecurityService.currentUser?.id
        Long modelId = model.id

        executorService.submit {
            auditService.withDefaultAuthorId(authorId) {
                Asset updated = Asset.get(id)
                try {
                    //do the hard work
                    assetService.storeAssetWithSteam(updated, "application/vnd.openxmlformats-officedocument.wordprocessingml.document",) { OutputStream out ->
                        new ChangelogGenerator(auditService, dataClassService).generateChangelog(Model.get(modelId), out)
                    }

                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your model changelog is ready. Use Download button to download it."
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} with id=${id}", e

                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = "Error generating model changelog report" +":$e"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }
        return asset.id;
    }

}
