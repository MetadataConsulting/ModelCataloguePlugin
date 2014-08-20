package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.PublishedElements
import org.modelcatalogue.core.util.Relationships
import org.modelcatalogue.core.util.SimpleListWrapper


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
//        List<PublishedElement> items
//        items.addAll(classification.classifies)
//
//        SimpleListWrapper<PublishedElement> elements = new SimpleListWrapper<PublishedElement>(
//                base: "/${resourceName}/${params.id}/classifies",
//                total: items.size(),
//                items: items,
//        )
//        reportCapableRespond new PublishedElements(list: withLinks(elements))


        reportCapableRespond new PublishedElements(list: Lists.fromCriteria(params, PublishedElement, , "classifies"){
            eq "classifications", classification
        })

    }

}
