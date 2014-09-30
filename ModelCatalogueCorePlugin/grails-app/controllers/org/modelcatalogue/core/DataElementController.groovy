package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.DataArchitectService
import org.modelcatalogue.core.util.Lists

class DataElementController extends AbstractPublishedElementController<DataElement> {

    DataArchitectService dataArchitectService

    DataElementController() {
        super(DataElement, false)
    }

    @Override
    def index(Integer max) {
        if (params.status == 'uninstantiated') {
            handleParams(max)
            reportCapableRespond Lists.wrap(params, resource, basePath, dataArchitectService.uninstantiatedDataElements(params))
            return
        }
        super.index(max)
    }


}
