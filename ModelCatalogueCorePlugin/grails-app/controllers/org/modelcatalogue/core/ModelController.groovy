package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements

class ModelController extends AbstractCatalogueElementController<Model> {

    ModelController() {
        super(Model)
    }

    def publishedElementService

    @Override
    def index(Integer max) {
        setSafeMax(max)
        Integer total = publishedElementService.count(params, Model)
        def list = publishedElementService.list(params, Model)
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
