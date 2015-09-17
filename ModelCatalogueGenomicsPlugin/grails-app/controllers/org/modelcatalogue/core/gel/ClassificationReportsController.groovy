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

        def assetName="$classification.name report as MS Word Document"
        def assetFileName="${classification.name}-${classification.status}-${classification.version}.${params.jasperFormat}"


        def assetPendingDesc="Your classification report  will be available in this asset soon. Use Refresh action to reload"
        def assetFinalizedDesc="Your classification is ready. Use Download button to download it."
        def assetErrorDesc="Error generating classification report"
        def assetMimeType="application/${params.jasperFormat}"

        def assetId=storeAssetAsDocx(classification,assetName,assetMimeType,assetPendingDesc,assetFinalizedDesc,assetErrorDesc,assetFileName)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    private def storeAssetAsDocx(Classification classification,String assetName=null,String mimeType="application/octet-stream",String assetPendingDesc="",String assetFinalizedDesc="",String assetErrorDesc="",String originalFileName="unknown"){
        Asset asset = new Asset(
                name:assetName ,
                originalFileName: originalFileName,
                description: assetPendingDesc,
                status: ElementStatus.PENDING,
                contentType: mimeType,
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
                    assetService.storeAssetWithSteam(updated, mimeType) { OutputStream out ->
                        new ClassificationToDocxExporter(Classification.get(classificationId)).export(out)
                    }

                   

                    updated.status = ElementStatus.FINALIZED
                    updated.description = assetFinalizedDesc
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} with id=${id}", e

                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = assetErrorDesc+":$e"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }
        return asset.id;
    }
}
