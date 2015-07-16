package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class DataClassController extends AbstractCatalogueElementController<DataClass> {

    def dataClassService

    DataClassController() {
        super(DataClass, false)
    }

    @Override
    def index(Integer max) {
        if (!params.boolean("toplevel")) {
            return super.index(max)
        }
        if(params.status && params.status.toLowerCase() != 'finalized' && !modelCatalogueSecurityService.hasRole('VIEWER')) {
            notAuthorized()
            return
        }
        handleParams(max)

        respond Lists.wrap(params, "/${resourceName}/", dataClassService.getTopLevelDataClasses(params))
    }

}
