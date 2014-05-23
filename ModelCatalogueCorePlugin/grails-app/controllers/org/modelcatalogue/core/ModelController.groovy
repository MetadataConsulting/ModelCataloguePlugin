package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWrapper

class ModelController extends AbstractExtendibleElementController<Model> {

    def modelService

    ModelController() {
        super(Model, false)
    }

    @Override
    def index(Integer max) {
        if (!params.boolean("toplevel")) {
            return super.index(max)
        }
        setSafeMax(max)

        ListAndCount topLevel = modelService.getTopLevelModels(params)

        def links = ListWrapper.nextAndPreviousLinks(params, "/${resourceName}/${params.status ? params.status : ''}", topLevel.count)
        respondWithReports new Elements(
                total: topLevel.count,
                items: topLevel.list,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0,
                itemType: resource
        )
    }

}
