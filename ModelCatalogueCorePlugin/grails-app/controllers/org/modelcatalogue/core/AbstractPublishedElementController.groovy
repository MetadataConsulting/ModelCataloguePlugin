package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements

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

        respondWithLinks new Elements(
                base: "/${resourceName}/",
                total: total,
                items: list
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

        respondWithLinks new Elements(
                base: "/${resourceName}/${params.id}/history",
                items: list,
                total: total
        )
    }



    protected Map getParametersToBind() {
        Map ret = super.parametersToBind
        ret.remove 'modelCatalogueId'
        ret.remove 'versionNumber'
        ret
    }

}
