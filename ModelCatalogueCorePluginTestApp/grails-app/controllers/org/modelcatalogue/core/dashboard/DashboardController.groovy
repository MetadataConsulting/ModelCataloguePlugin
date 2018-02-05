package org.modelcatalogue.core.dashboard

import groovy.transform.CompileStatic
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.PublishedStatus
import org.modelcatalogue.core.util.SortQuery
import org.modelcatalogue.core.view.AssetViewModel
import org.modelcatalogue.core.view.DataModelViewModel
import org.springframework.context.MessageSource

@CompileStatic
class DashboardController {

    DashboardService dashboardService
    MessageSource messageSource

    static allowedMethods = [
            index: 'GET'
    ]

    def index(DashboardIndexCommand cmd) {

        if  ( cmd.hasErrors() ) {
            flash.error = messageSource.getMessage('dashboard.params.notvalid', [] as Object[], 'Invalid Parameters', request.locale)
            return
        }

        SearchStatusQuery searchStatusQuery = cmd.toSearchStatusQuery()
        SortQuery sortQuery = cmd.toSortQuery()
        PaginationQuery paginationQuery = cmd.toPaginationQuery()

        List catalogueElementList = dashboardService.findAllBySearchStatusQuery(cmd.metadataDomain,
                searchStatusQuery,
                sortQuery,
                paginationQuery) ?: [] as List<DataModelViewModel>
        int total = dashboardService.countAllBySearchStatusQuery(cmd.metadataDomain, searchStatusQuery)
        [
                metadataDomain: cmd.metadataDomain,
                metadataDomainList: [MetadataDomain.DATA_MODEL, MetadataDomain.DATA_ELEMENT],
                sortQuery: sortQuery,
                paginationQuery: paginationQuery,
                search: cmd.search,
                status: cmd.status,
                catalogueElementList: catalogueElementList,
                total: total,
        ]
    }

}
