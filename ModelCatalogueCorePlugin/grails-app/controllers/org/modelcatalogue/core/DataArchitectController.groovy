package org.modelcatalogue.core

import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.dataarchitect.CSVService
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile

class DataArchitectController extends AbstractRestfulController<CatalogueElement> {

    static responseFormats = ['json', 'xlsx']

    def dataArchitectService
    def modelService
    def executorService
    @Autowired AuditService auditService
    @Autowired AssetService assetService
    @Autowired CSVService csvService

    DataArchitectController() {
        super(CatalogueElement, false)
    }

    def index(){}

    def uninstantiatedDataElements(Integer max){
        handleParams(max)
        respond Lists.wrap(params, DataElement, "/dataArchitect/uninstantiatedDataElements", dataArchitectService.uninstantiatedDataElements(params))
    }


    def metadataKeyCheck(Integer max){
        handleParams(max)
        respond Lists.wrap(params, DataElement, "/dataArchitect/metadataKeyCheck", dataArchitectService.metadataKeyCheck(params))
    }

    def getSubModelElements(){
        Long id = params.long('modelId') ?: params.long('id')
        respond Lists.lazy(params, DataElement, "/dataArchitect/getSubModelElements") {
            if (id){
                Model model = Model.get(id)
                ListWithTotalAndType<Model> subModels = modelService.getSubModels(model)
                return modelService.getDataElementsFromModels(subModels.items).items
            }
            return []
        }
    }

    def findRelationsByMetadataKeys(Integer max){
        handleParams(max)
        ListWithTotal results
        def keyOne = params.keyOne
        def keyTwo = params.keyTwo
        if(keyOne && keyTwo) {
            try {
                results = dataArchitectService.findRelationsByMetadataKeys(keyOne, keyTwo, params)
            } catch (Exception e) {
                println(e)
                return
            }

            //FIXME we need new method to do this and integrate it with the ui
            try {
                dataArchitectService.actionRelationshipList(results.items)
            } catch (Exception e) {
                println(e)
                return
            }

            respond Lists.wrap(params, Relationship, "/dataArchitect/findRelationsByMetadataKeys", results)

        }else{
            respond "please enter keys"
        }

    }

    def modelsFromCSV(){
        MultipartFile file = request.getFile('csv')

        if (!file) {
            respond status: HttpStatus.BAD_REQUEST
            return
        }

        List<Object> elements = []

        file.inputStream.withReader {
            elements = dataArchitectService.matchModelsWithCSVHeaders(csvService.readHeaders(it, params.separator ?: ';'))
        }

        respond elements
    }

    def elementsFromCSV(){
        MultipartFile file = request.getFile('csv')

        if (!file) {
            respond status: HttpStatus.BAD_REQUEST
            return
        }

        List<Object> elements = []

        file.inputStream.withReader {
            elements = dataArchitectService.matchDataElementsWithCSVHeaders(csvService.readHeaders(it, params.separator ?: ';'))
        }

        respond elements
    }

    def generateSuggestions() {
        try {
            dataArchitectService.generateMergeModelActions()
            respond status: HttpStatus.OK
        } catch (e) {
            log.error("Error generating suggestions", e)
            respond status: HttpStatus.BAD_REQUEST
        }
    }

    def gelXmlShredderModel() {
        Model model=Model.get(params.id)

        if (!model) {
            render status: HttpStatus.NOT_FOUND
            return
        }
    
        Asset asset = new Asset(
                name: "$model.name Model for XML Shredder",
                originalFileName: "${model.name}-gel-xml-shredder.xml",
                description: "Your XML will be available in this asset soon. Use Refresh action to reload",
                status: ElementStatus.PENDING,
                contentType: "text.xml",
                size: 0
        )
    
        asset.save(flush: true, failOnError: true)
    
        Long id = asset.id
    
        Long authorId = modelCatalogueSecurityService.currentUser?.id
    
        executorService.submit {
            auditService.withDefaultAuthorId(authorId) {
                Asset updated = Asset.get(id)
                try {
                    
                    String result= modelService.gelXmlModelShredder(model)
                    assetService.storeAssetFromInputStream( new ByteArrayInputStream(result.bytes), "text/xml", updated)
                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your Xml model  is ready. Use Download button to download it."
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} xml model ${id}", e
    
                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = "Error generating xml model for shredder: ${e}"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }
    
        response.setHeader("X-Asset-ID", asset.id.toString())    
        redirect

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

    Asset asset = new Asset(
            name: "$model.name XML Schema(XSD)",
            originalFileName: model.ext.get(ModelService.XSD_SCHEMA_NAME)?model.ext.get(ModelService.XSD_SCHEMA_NAME)+"v${model.ext.get(ModelService.XSD_SCHEMA_VERSION)}.xsd"
            :"${model.name}.xsd",
            description: "Your XSD will be available in this asset soon. Use Refresh action to reload",
            status: ElementStatus.PENDING,
            contentType: "application/xsd",
            size: 0
    )

    asset.save(flush: true, failOnError: true)

    Long id = asset.id

    Long authorId = modelCatalogueSecurityService.currentUser?.id

    executorService.submit {
        auditService.withDefaultAuthorId(authorId) {
            Asset updated = Asset.get(id)
            try {
                
                String result= modelService.printXSDModel(model)

                assetService.storeAssetFromInputStream( new ByteArrayInputStream(result.bytes), "application/xsd", updated)
                updated.originalFileName="${model.ext.get(ModelService.XSD_SCHEMA_NAME)}-v${model.ext.get(ModelService.XSD_SCHEMA_VERSION)}.xsd"
                updated.status = ElementStatus.FINALIZED
                updated.description = "Your XSD  is ready. Use Download button to download it."
                updated.save(flush: true, failOnError: true)
            } catch (e) {
                log.error "Exception of type ${e.class} xsd schema ${id}", e

                updated.refresh()
                updated.status = ElementStatus.FINALIZED
                updated.name = updated.name + " - Error during generation"
                updated.description = "Error generating xsd: ${e}"
                updated.save(flush: true, failOnError: true)
            }
        }
    }

    response.setHeader("X-Asset-ID", asset.id.toString())

    redirect controller: 'asset', id: asset.id, action: 'show'
    

}


}
