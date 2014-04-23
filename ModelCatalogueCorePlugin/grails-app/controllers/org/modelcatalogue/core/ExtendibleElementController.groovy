package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements

class ExtendibleElementController extends AbstractCatalogueElementController<ExtendibleElement> {

    ExtendibleElementController() {
        super(ExtendibleElement, true)
    }

    def publishedElementService

    @Override
    def index(Integer max) {
        setSafeMax(max)
        Integer total = publishedElementService.count(params, ExtendibleElement)
        def list = publishedElementService.list(params, ExtendibleElement)
        def links = nextAndPreviousLinks("/${resourceName}/${params.status ? params.status : ''}", total)
        respond new Elements(
                total: total,
                items: list,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0,
                itemType: resource
        )
    }

}
