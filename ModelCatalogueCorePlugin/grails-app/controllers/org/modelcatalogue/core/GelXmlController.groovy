package org.modelcatalogue.core

import org.modelcatalogue.core.audit.AuditService;
import org.modelcatalogue.core.dataarchitect.CSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class GelXmlController {

    def dataArchitectService
    def modelService
    def executorService
    AuditService auditService
    AssetService assetService

    SecurityService modelCatalogueSecurityService
    ElementService elementService

    def index() { }


    def gelXmlShredderModel() {
        Model model=Model.get(params.id)

        if (!model) {
            render status: HttpStatus.NOT_FOUND
            return
        }
        def assetName="$model.name Model for XML Shredder"
        def assetFileName="${model.name}-gel-xml-shredder.xml"
        def assetPendingDesc="Your XML Model will be available in this asset soon. Use Refresh action to reload"
        def assetFinalizedDesc="Your Xml model  is ready. Use Download button to download it."
        def assetErrorDesc="Error generating xml model"
        def assetMimeType="application/octet-stream"
        
        //TODO pass this operation inside of closure
        def result=modelService.gelXmlModelShredder(model)
        
        Closure closure={return result}

        def assetId=storeAssetFromString(model,closure,assetName,assetMimeType,assetPendingDesc,assetFinalizedDesc,assetErrorDesc,assetFileName)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'

    }


    /**
     * Generate xsd schema as an asset
     * @return redirect to asset controller
     */
    def modelsToXSD(){
        def model=Model.get(params.id)


        if (!model) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        def assetName="$model.name XML Schema(XSD)"
        def assetFileName=model.ext.get(ModelService.XSD_SCHEMA_NAME)?model.ext.get(ModelService.XSD_SCHEMA_NAME)+"v${model.ext.get(ModelService.XSD_SCHEMA_VERSION)}.xsd":" Invalid Asset $model.name XML Schema(XSD).xsd"


        def assetPendingDesc="Your XSD  will be available in this asset soon. Use Refresh action to reload"
        def assetFinalizedDesc="Your XSD is ready. Use Download button to download it."
        def assetErrorDesc="Error generating xsd"
        def assetMimeType="application/xsd"
        Closure closure={return modelService.printXSDModel(model)}

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
                    //String result= modelService.gelXmlModelShredder(model)
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
