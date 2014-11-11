package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class ModelController extends AbstractCatalogueElementController<Model> {

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

        respond Lists.wrap(params, "/${resourceName}/", modelService.getTopLevelModels(params))
    }

}
