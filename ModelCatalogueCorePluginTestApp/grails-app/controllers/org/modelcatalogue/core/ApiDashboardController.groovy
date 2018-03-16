package org.modelcatalogue.core

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.dashboard.DashboardService
import org.modelcatalogue.core.dashboard.SearchStatusQuery
import org.modelcatalogue.core.util.GormUrlName
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.MetadataDomainEntity
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

    @CompileDynamic
    def catalogueElements(Long dataModelId) {
        List<GormUrlName> dataElementList = dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.DATA_ELEMENT, null, null, null).collect {
            new GormUrlName(dataModelId: dataModelId, gormUrl: MetadataDomainEntity.stringRepresentation(new MetadataDomainEntity(domain: MetadataDomain.DATA_ELEMENT, id: it.id)), name: it.name)
        } as List<GormUrlName>
        List<GormUrlName> dataClassList = dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.DATA_CLASS, null, null, null).collect {
            new GormUrlName(dataModelId: dataModelId, gormUrl: MetadataDomainEntity.stringRepresentation(new MetadataDomainEntity(domain: MetadataDomain.DATA_CLASS, id: it.id)), name: it.name)
        } as List<GormUrlName>
        List<GormUrlName> enumeratedTypeList = dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.ENUMERATED_TYPE, null, null, null).collect {
            new GormUrlName(dataModelId: dataModelId, gormUrl: MetadataDomainEntity.stringRepresentation(new MetadataDomainEntity(domain: MetadataDomain.ENUMERATED_TYPE, id: it.id)), name: it.name)
        } as List<GormUrlName>
        List<GormUrlName> dataTypeList = dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.DATA_TYPE, null, null, null).collect {
            new GormUrlName(dataModelId: dataModelId, gormUrl: MetadataDomainEntity.stringRepresentation(new MetadataDomainEntity(domain: MetadataDomain.DATA_TYPE, id: it.id)), name: it.name)
        }
        List<GormUrlName> measurementUnitList = dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.MEASUREMENT_UNIT, null, null, null).collect {
            new GormUrlName(dataModelId: dataModelId, gormUrl: MetadataDomainEntity.stringRepresentation(new MetadataDomainEntity(domain: MetadataDomain.MEASUREMENT_UNIT, id: it.id)), name: it.name)
        } as List<GormUrlName>
        List<GormUrlName> businessRuleList = dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.BUSINESS_RULE, null, null, null).collect {
            new GormUrlName(dataModelId: dataModelId, gormUrl: MetadataDomainEntity.stringRepresentation(new MetadataDomainEntity(domain: MetadataDomain.BUSINESS_RULE, id: it.id)), name: it.name)
        } as List<GormUrlName>
        List<GormUrlName> tagsList = dashboardService.findAllBySearchStatusQuery(dataModelId, MetadataDomain.TAG, null, null, null).collect {
            new GormUrlName(dataModelId: dataModelId, gormUrl: MetadataDomainEntity.stringRepresentation(new MetadataDomainEntity(domain: MetadataDomain.TAG, id: it.id)), name: it.name)
        } as List<GormUrlName>
        render(contentType: "application/json") {
            [
                dataElements    : dataElementList,
                dataClasses     : dataClassList,
                enumeratedTypes : enumeratedTypeList,
                dataTypes       : dataTypeList,
                measurementUnits: measurementUnitList,
                businessRules   : businessRuleList,
                tags            : tagsList,
            ]
        }
    }
}
