package org.modelcatalogue.core.gel

import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetService
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.audit.AuditService
import org.springframework.http.HttpStatus


/**
 * Different actions related to generate json actions 
 * 
 * @author csfercoci
 *
 */
class GelJsonController {

    def gelJsonService
    def executorService


    SecurityService modelCatalogueSecurityService

    def index() { }

    /**
     * Generate xml model for xml shredder, it is stored as an asset 
     * @return  json 
     */
    def printDiseaseOntology() {
        Model model=Model.get(params.id)

        if (!model) {
            render status: HttpStatus.NOT_FOUND
            return
        }
        
       render (text: gelJsonService.printDiseaseOntology(model));
        

    }


    /**
     * Generate xsd schema as an asset
     * @return redirect to asset controller
     */
    def generateXSD(){
        def model=Model.get(params.id)


        if (!model) {
            render status: HttpStatus.NOT_FOUND
            return
        }
        
        

     
    }


    
}
