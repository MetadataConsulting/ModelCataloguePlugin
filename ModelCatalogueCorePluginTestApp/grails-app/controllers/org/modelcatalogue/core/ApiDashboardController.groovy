package org.modelcatalogue.core

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.dashboard.DashboardService
import org.modelcatalogue.core.dashboard.SearchQuery
import org.modelcatalogue.core.dashboard.SearchScope
import org.modelcatalogue.core.util.GormUrlName
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.MetadataDomainEntity

@CompileStatic
class ApiDashboardController {
    static responseFormats = ['json']

    DashboardService dashboardService

    def dataModels() {
        render(contentType: "application/json") {
            [dataModels: dashboardService.findAllDataModel(false)]
        }
    }

    @CompileDynamic
    private List<GormUrlName> findAllGormUrlName(Long dataModelId, MetadataDomain metadataDomain) {
        SearchQuery query = new SearchQuery(metadataDomain: metadataDomain,
                dataModelId: dataModelId,
                searchScope: SearchScope.DATAMODEL_AND_IMPORTS)
        dashboardService.search(query, null, null)?.viewModels?.collect {
            final String gormUrl =  MetadataDomainEntity.stringRepresentation(new MetadataDomainEntity(domain: metadataDomain, id: it.id))
            new GormUrlName(dataModelId: dataModelId, gormUrl: gormUrl, name: it.name)
        } as List<GormUrlName>
    }

    def catalogueElements(Long dataModelId) {
        render(contentType: "application/json") {
            [
                dataElements    : findAllGormUrlName(dataModelId, MetadataDomain.DATA_ELEMENT),
                dataClasses     : findAllGormUrlName(dataModelId, MetadataDomain.DATA_CLASS),
                enumeratedTypes : findAllGormUrlName(dataModelId, MetadataDomain.ENUMERATED_TYPE),
                dataTypes       : findAllGormUrlName(dataModelId, MetadataDomain.DATA_TYPE),
                measurementUnits: findAllGormUrlName(dataModelId, MetadataDomain.MEASUREMENT_UNIT),
            ]
        }
    }
}
