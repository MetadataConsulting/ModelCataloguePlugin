package org.modelcatalogue.core

import org.hibernate.Criteria
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
//        List items = []
//        def classifications = classification.classifies
//        items.addAll(classifications)
//
//        SimpleListWrapper<PublishedElement> elements = new SimpleListWrapper<PublishedElement>(
//                base: "/${resourceName}/${params.id}/classifies",
//                total: items.size(),
//                items: items,
//        )
//        reportCapableRespond new PublishedElements(list: withLinks(elements))


//        def x = PublishedElement.withCriteria {
//            ", classification
//        }


        def c = PublishedElement.createCriteria()
        def results = c.list {
            classifications{
                idEq(classification.id)
            }
        }



        reportCapableRespond new PublishedElements(list: Lists.fromCriteria(params, PublishedElement, "/${resourceName}/${params.id}/classifies", "classifies"){
            classifications{
                idEq(classification.id)
            }
        })

    }

}
