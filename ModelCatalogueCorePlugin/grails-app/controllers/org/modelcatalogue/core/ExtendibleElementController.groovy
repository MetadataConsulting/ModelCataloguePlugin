package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements

class ExtendibleElementController extends AbstractExtendibleElementController<ExtendibleElement> {

    ExtendibleElementController() {
        super(ExtendibleElement, true)
    }

//    def publishedElementService
//
//    @Override
//    def index(Integer max) {
//        setSafeMax(max)
//        Integer total = publishedElementService.count(params, ExtendibleElement)
//        def list = publishedElementService.list(params, ExtendibleElement)
//
//        respondWithLinks new Elements(
//                base: "/${resourceName}/${params.status ? params.status : ''}",
//                total: total,
//                items: list
//        )
//    }

}
