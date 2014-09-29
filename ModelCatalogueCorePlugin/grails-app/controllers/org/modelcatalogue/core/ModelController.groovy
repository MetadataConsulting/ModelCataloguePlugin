package org.modelcatalogue.core

import grails.transaction.Transactional
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

        reportCapableRespond publishedElementService.finalizeTree(instance)
    }

}
