package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists

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

        reportCapableRespond Lists.wrap(params, "/${resourceName}/", "elements", modelService.getTopLevelModels(params))
    }

}
