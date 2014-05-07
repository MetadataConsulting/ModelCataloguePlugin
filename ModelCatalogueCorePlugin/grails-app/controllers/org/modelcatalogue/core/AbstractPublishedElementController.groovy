package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper

class AbstractPublishedElementController<T> extends AbstractCatalogueElementController<T> {

    def publishedElementService

    AbstractPublishedElementController(Class<T> type, boolean readOnly) {
        super(type, readOnly)
    }

    @Override
    def index(Integer max) {
        setSafeMax(max)
        Integer total = publishedElementService.count(params, resource)
        def list = publishedElementService.list(params, resource)
        def links = ListWrapper.nextAndPreviousLinks(params, "/${resourceName}/${params.status ? params.status : ''}", total)
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

    def history(Integer max){
        setSafeMax(max)
        PublishedElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        def customParams = [:]
        customParams.putAll params

        customParams.sort = 'versionNumber'

        int total = resource.countByModelCatalogueIdLike "$element.bareModelCatalogueId%"
        def list = resource.findAllByModelCatalogueIdLike "$element.bareModelCatalogueId%", customParams
        def links = ListWrapper.nextAndPreviousLinks(params, "/${resourceName}/${params.id}/history", total)

        respond new Elements(
                items: list,
                previous: links.previous,
                next: links.next,
                total: total,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0,
                itemType: resource
        )
    }

}
