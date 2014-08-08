package org.modelcatalogue.core.actions

import org.modelcatalogue.core.AbstractRestfulController
import org.modelcatalogue.core.util.Lists

class BatchController extends AbstractRestfulController<Batch> {

    @Override
    protected String getRoleForSaveAndEdit() {
        "ADMIN"
    }

    BatchController() {
        super(Batch)
    }

    @Override
    def index(Integer max) {
        if (!modelCatalogueSecurityService.hasRole('ADMIN')) {
            notAuthorized()
            return
        }
        handleParams(max)
        reportCapableRespond Lists.fromCriteria(params, resource, "/${resourceName}/") {
            eq 'archived', params.boolean('archived') || params.status == 'archived'
        }
    }
}
