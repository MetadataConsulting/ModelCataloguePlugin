package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.modelcatalogue.core.dashboard.DashboardService
import org.modelcatalogue.core.dashboard.SearchStatusQuery
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery

@CompileStatic
class ApiDashboardController {
    static responseFormats = ['json']

    DashboardService dashboardService

    def dataModels() {
        render(contentType: "application/json") {
            [dataModels: dashboardService.findAllDataModel()]
        }
    }

    def catalogueElements(Long dataModelId) {
        render(contentType: "application/json") {
            [
                dataElements    : dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.DATA_ELEMENT, null, null, null),
                dataClasses     : dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.DATA_CLASS, null, null, null),
                enumeratedTypes : dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.ENUMERATED_TYPE, null, null, null),
                dataTypes       : dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.DATA_TYPE, null, null, null),
                measurementUnits: dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.MEASUREMENT_UNIT, null, null, null),
                businessRules   : dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.BUSINESS_RULE, null, null, null),
                tags            : dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.TAG, null, null, null)
            ]
        }
    }
}
