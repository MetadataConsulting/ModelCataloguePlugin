package org.modelcatalogue.core.gel

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
    
    def index() { }
    
    def gereportDoc() {
        Classification classification = Classification.get(params.id)

        def assetId=storeAssetAsDocx(classification)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    private def storeAssetAsDocx(Classification classification){
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
                        new ClassificationToDocxExporter(Classification.get(classificationId)).export(out)
                    }

                   

                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your classification is ready. Use Download button to download it."
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} with id=${id}", e

                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = "Error generating classification report" +":$e"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }
        return asset.id;
    }
}
