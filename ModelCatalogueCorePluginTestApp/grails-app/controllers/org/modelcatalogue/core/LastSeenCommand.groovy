package org.modelcatalogue.core

import grails.validation.Validateable
import org.modelcatalogue.core.api.ElementStatusUtils
import org.modelcatalogue.core.dashboard.DashboardDropdown
import org.modelcatalogue.core.dashboard.SearchStatusQuery
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery

@Validateable
class LastSeenCommand {

    Integer max = 25
    String order = 'desc'
    String sort = 'authenticationDate'
    Integer offset = 0

    static constraints = {
        offset nullable: true, min: 0
        sort nullable: true, inList: ['authenticationDate', 'username']
        order nullable: true, inList: ['asc', 'desc']
    }

    SortQuery toSortQuery() {
        new SortQuery(order: order, sort: sort)
    }

    PaginationQuery toPaginationQuery() {
        new PaginationQuery(max: max, offset: offset)
    }
}
