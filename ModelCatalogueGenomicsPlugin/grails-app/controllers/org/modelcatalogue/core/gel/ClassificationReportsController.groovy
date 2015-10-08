package org.modelcatalogue.core.gel

import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ModelService
import org.modelcatalogue.core.publishing.changelog.ChangelogGenerator

import java.util.concurrent.ExecutorService


import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetService
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.SecurityService;
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
    ModelService modelService
    
    def index() { }
    
    def gereportDoc() {
        Model classification = Model.get(params.id)

        def assetId=storeAssetAsDocx(classification)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    private def storeAssetAsDocx(Model model){
        Asset asset = new Asset(
                name: "$model.name report as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}.docx",
                description: "Your model report  will be available in this asset soon. Use Refresh action to reload",
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
                        new ModelToDocxExporter(Model.get(modelId), modelService).export(out)
                    }

                   

                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your model is ready. Use Download button to download it."
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} with id=${id}", e

                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = "Error generating model report" +":$e"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }
        return asset.id;
    }

    def changelogDoc() {
        Model model = Model.get(params.id)

        def assetId = storeChangelogAsset(model)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    private def long storeChangelogAsset(Model model){
        Asset asset = new Asset(
                name: "$model.name changelog as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}-changelog.docx",
                description: "Your model report will be available in this asset soon. Use Refresh action to reload",
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
                        new ChangelogGenerator(auditService, modelService).generateChangelog(Model.get(modelId), out)
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
