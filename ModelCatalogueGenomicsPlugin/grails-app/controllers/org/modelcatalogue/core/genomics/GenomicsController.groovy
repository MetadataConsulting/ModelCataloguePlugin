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
class GenomicsController {

    def gelJsonService
    def assetService

    def exportRareDiseaseHPOAndClinicalTests() {
        DataClass model = DataClass.get(params.id)

        Long classId = model.getId()

        Long assetId = assetService.storeReportAsAsset(model.dataModel,
            name: "${model.name} report as Json",
            originalFileName: "${model.name}-${model.status}-${model.version}.json",
            contentType: "application/json",
        ) {
            it << gelJsonService.printDiseaseOntology(DataClass.get(classId))
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
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
