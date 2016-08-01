package org.modelcatalogue.core.gel

import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetService
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.audit.AuditService
import org.springframework.http.HttpStatus

import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 * Different actions related to generate json actions 
 * 
 * @author csfercoci
 *
 */
class GelJsonController {

    private static final DateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmss")

    def gelJsonService
    def executorService
    AuditService auditService
    AssetService assetService



    SecurityService modelCatalogueSecurityService

    def index() { }


    /**
     * Generate Rare Disease json list 
     * @return redirect to asset controller
     */
    def printDiseaseOntology(){
        def model=Model.get(params.id)
        def id = model.latestVersionId ?: model.id


        if (!model || id!=11144) {
            render status: HttpStatus.FORBIDDEN
            return
        }

        def assetName="$model.name version "
        def assetFileName="${model.name}_v${model.version}_${FORMAT.format(model.lastUpdated)}.json"


        def assetPendingDesc="Your Rare Disease Ontology list as JSON   will be available in this asset soon. Use Refresh action to reload"
        def assetFinalizedDesc="Your JSON is ready. Use Download button to download it."
        def assetErrorDesc="Error generating json"
        def assetMimeType="application/json"
        Closure closure={return gelJsonService.printDiseaseOntology(model)}

        def assetId=storeAssetFromString(model,closure,assetName,assetMimeType,assetPendingDesc,assetFinalizedDesc,assetErrorDesc,assetFileName)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    /**
     * Generate Cancer Type, Subtype json list
     * @return redirect to asset controller
     */

    def printCancerTypeList(){
        def model=Model.get(params.id)
        def id = model.latestVersionId ?: model.id


        if (!model || id!=37298) {
            render status: HttpStatus.FORBIDDEN
            return
        }

        def assetName="$model.name version "
        def assetFileName="${model.name}_v${model.version}_${FORMAT.format(model.lastUpdated)}.json"


        def assetPendingDesc="Your Cancer Type and Subtypes list as JSON   will be available in this asset soon. Use Refresh action to reload"
        def assetFinalizedDesc="Your JSON is ready. Use Download button to download it."
        def assetErrorDesc="Error generating json"
        def assetMimeType="application/json"
        Closure closure={return gelJsonService.printCancerTypeList(model)}

        def assetId=storeAssetFromString(model,closure,assetName,assetMimeType,assetPendingDesc,assetFinalizedDesc,assetErrorDesc,assetFileName)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }



    /**
     * Store a  asset from passed closure. In closure you can pass different operations but must return a string as aresult. closure will be operated in a different thread
     *
     * @param model Model class for which will be created the current asset
     * @param closure must return a string
     * @return assetID
     */
    private def storeAssetFromString(Model model,Closure closure,String assetName=null,String mimeType="application/octet-stream",String assetPendingDesc="",String assetFinalizedDesc="",String assetErrorDesc="",String originalFileName="unknown"){
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

        executorService.submit {
            auditService.withDefaultAuthorId(authorId) {
                Asset updated = Asset.get(id)
                try {
                    //String result= XmlService.gelXmlModelShredder(model)
                    assetService.storeAssetFromInputStream( new ByteArrayInputStream(closure.call().bytes),mimeType, updated)

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
