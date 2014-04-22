package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements

class PublishedElementController extends AbstractCatalogueElementController<PublishedElement> {

    def publishedElementService

    PublishedElementController() {
        super(PublishedElement, true)
    }

    @Override
    def index(Integer max) {
        setSafeMax(max)
        Integer total = publishedElementService.count(params)
        def list = publishedElementService.list(params)
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
