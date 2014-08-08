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
        handleParams(max)
        reportCapableRespond Lists.fromCriteria(params, resource, "/${resourceName}/") {
            if (!params.boolean('archived')) {
                eq 'archived', false
            }
        }
    }

}
