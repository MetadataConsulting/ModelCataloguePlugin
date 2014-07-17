package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWithTotal

class ModelController extends AbstractPublishedElementController<Model> {

    def modelService

    ModelController() {
        super(Model, false)
    }

    @Override
    def index(Integer max) {
        if (!params.boolean("toplevel")) {
            return super.index(max)
        }
        handleParams(max)

        ListWithTotal topLevel = modelService.getTopLevelModels(params)

        respondWithLinks new Elements(
                base: "/${resourceName}/",
                total: topLevel.total,
                items: topLevel.items
        )
    }

}
