package org.modelcatalogue.core

import org.modelcatalogue.core.util.DataModelFilter

class DashboardController {

    static responseFormats = ['json', 'xlsx']

    def dataModelService

    def index() {
        response.addHeader('Expires', '-1')
        respond(dataModelService.getStatistics(overridableDataModelFilter))
    }

    protected DataModelFilter getOverridableDataModelFilter() {
        if (params.dataModel) {
            DataModel dataModel = DataModel.get(params.long('dataModel'))
            if (dataModel) {
                return DataModelFilter.includes(dataModel)
            }
        }
        dataModelService.dataModelFilter
    }

}

