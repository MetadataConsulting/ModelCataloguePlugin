package org.modelcatalogue.core.dashboard

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery

@CompileStatic
class DashboardController {

    DataModelGormService dataModelGormService

    static allowedMethods = [
            index: 'GET'
    ]

    def index(DashboardIndexCommand cmd) {
        if  ( cmd.hasErrors() ) {
            render status: 404
            return
        }

        SearchStatusQuery searchStatusQuery = cmd.toSearchStatusQuery()
        SortQuery sortQuery = cmd.toSortQuery()
        PaginationQuery paginationQuery = cmd.toPaginationQuery()
        List<DataModel> dataModelList = dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, sortQuery, paginationQuery) ?: [] as List<DataModel>
        Number count = dataModelGormService.countAllBySearchStatusQuery(searchStatusQuery)
        int total =  count != null ? (count as int) : 0
        [
                sortQuery: sortQuery,
                paginationQuery: paginationQuery,
                search: cmd.search,
                status: cmd.status,
                models: dataModelList,
                total: total
        ]
    }
}
