package org.modelcatalogue.core.genomics

import org.modelcatalogue.core.DataClass
import org.springframework.http.HttpStatus

/**
 * Controller for GEL specific reports.
 */
class GenomicsController {


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