package org.modelcatalogue.core.dashboard

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.PublishedStatus
import org.modelcatalogue.core.util.SortQuery
import org.modelcatalogue.core.view.AssetViewModel
import org.modelcatalogue.core.view.DataModelViewModel
import org.springframework.context.MessageSource

import javax.annotation.PostConstruct

@CompileStatic
class DashboardController {

    DashboardService dashboardService
    MessageSource messageSource
    GrailsApplication grailsApplication
    String serverUrl

    @CompileDynamic
    @PostConstruct
    void init() {
        this.serverUrl = grailsApplication.config.grails.serverURL
    }

    static allowedMethods = [
            index: 'GET'
    ]

    def index(DashboardIndexCommand cmd) {

        String dataModelIdStr = cmd.dataModelId
        if ( dataModelIdStr == 'null') {
            dataModelIdStr = null
        }
        Long dataModelId = dataModelIdStr as Long

        if  ( cmd.hasErrors() ) {
            flash.error = messageSource.getMessage('dashboard.params.notvalid', [] as Object[], 'Invalid Parameters', request.locale)
            return
        }

        SearchStatusQuery searchStatusQuery = cmd.toSearchStatusQuery()
        SortQuery sortQuery = cmd.toSortQuery()
        PaginationQuery paginationQuery = cmd.toPaginationQuery()

        List catalogueElementList = dashboardService.findAllBySearchStatusQuery(dataModelId, cmd.metadataDomain,
                searchStatusQuery,
                sortQuery,
                paginationQuery) ?: [] as List<DataModelViewModel>
        List<IdName> dataModelList = dashboardService.findAllDataModel()
        int total = dashboardService.countAllBySearchStatusQuery(dataModelId, cmd.metadataDomain, searchStatusQuery)
        [
                dataModelList: dataModelList,
                metadataDomain: cmd.metadataDomain,
                metadataDomainList: dashboardService.metadataDomainList(),
                sortQuery: sortQuery,
                paginationQuery: paginationQuery,
                search: cmd.search,
                status: cmd.status,
                catalogueElementList: catalogueElementList,
                total: total,
                serverUrl: serverUrl,
                dataModelId: dataModelId
        ]
    }

}
