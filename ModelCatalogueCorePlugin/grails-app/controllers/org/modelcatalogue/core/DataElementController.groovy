package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.DataArchitectService
import org.modelcatalogue.core.util.Lists

class DataElementController extends AbstractCatalogueElementController<DataElement> {

    DataArchitectService dataArchitectService

    DataElementController() {
        super(DataElement, false)
    }

    @Override
    def index(Integer max) {
        if (params.status == 'uninstantiated') {
            handleParams(max)
            respond Lists.wrap(params, resource, basePath, dataArchitectService.uninstantiatedDataElements(params))
            return
        }
        if(params.status && params.status.toLowerCase() != 'finalized' && !modelCatalogueSecurityService.hasRole('VIEWER')) {
            notAuthorized()
            return
        }
        super.index(max)
    }


}
