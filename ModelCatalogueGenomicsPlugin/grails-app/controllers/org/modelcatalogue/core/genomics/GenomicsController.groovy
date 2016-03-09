package org.modelcatalogue.core.genomics

import groovy.util.logging.Log4j
import org.modelcatalogue.core.AbstractCatalogueElementController
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.http.HttpStatus

/**
 * Controller for GEL specific reports.
 */
@Log4j
class GenomicsController extends AbstractCatalogueElementController<DataClass> {

    def gelJsonService
    def executorService
    def auditService
    def springSecurity2SecurityService


    GenomicsController() {
        super(DataClass, false)
    }

    def exportRareDiseaseHPOAndClinicalTests() {
        DataClass model = DataClass.get(params.id)


        def asset = new Asset(
            name: "${model.name} report as Json",
            originalFileName: "${model.name}-${model.status}-${model.version}.json",
            description:"Asset Pending",
            status: ElementStatus.PENDING,
            contentType: "application/json",
            size: 0).save(flush:true )

        int id = springSecurity2SecurityService.currentUser.id
        executorService.submit() {
            try {
                auditService.withDefaultAuthorId(id)
                    {
                         assetService.storeAssetFromInputStream(
                            new ByteArrayInputStream(gelJsonService.printDiseaseOntology(model).bytes),
                            "application/json", asset
                        )

                        asset.status = ElementStatus.FINALIZED
                        asset.description = "Your Json is ready to download"
                        asset.save(flush:true)
                    }
            } catch (e) {
                log.error("Exception of type ${e.class} with id=${id} ", e)

                asset.refresh()
                asset.status = ElementStatus.FINALIZED
                asset.name = asset.name + " - Error during Json generation"
                asset.description = "Error generating Json"
                asset.save(flush:true)
            }

        }

        response.setHeader("X-Asset-ID",asset.id.toString())
        redirect controller: 'asset', id: asset.id, action: 'show'
    }




    def exportRareDiseases() {
        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        // get the id and and export the data class
        redirect url: "http://www.google.com/#${dataClass.name}"
    }

}
