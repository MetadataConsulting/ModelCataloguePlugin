package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListAndCount

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
        setSafeMax(max)

        ListAndCount topLevel = modelService.getTopLevelModels(params)

        respondWithLinks new Elements(
                base: "/${resourceName}/",
                total: topLevel.count,
                items: topLevel.list
        )
    }

}
