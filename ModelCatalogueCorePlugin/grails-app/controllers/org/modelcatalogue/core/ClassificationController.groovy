package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.PublishedElements


class ClassificationController extends AbstractCatalogueElementController<Classification> {

    ClassificationController() {
        super(Classification)
    }

    def classifies(Integer max){
        handleParams(max)
        Classification classification = queryForResource(params.id)
        if (!classification) {
            notFound()
            return
        }

        reportCapableRespond new PublishedElements(list: Lists.fromCriteria(params, PublishedElement, "/${resourceName}/${params.id}/classifies", "classifies"){
            eq "classifications", classification
        })

    }

}
