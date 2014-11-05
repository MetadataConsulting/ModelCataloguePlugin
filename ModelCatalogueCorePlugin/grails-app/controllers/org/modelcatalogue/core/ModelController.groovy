package org.modelcatalogue.core

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

        respond Lists.wrap(params, "/${resourceName}/", modelService.getTopLevelModels(params))
    }


    def finalizeTree() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }

        Model instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        respond elementService.finalizeTree(instance)
    }

}
