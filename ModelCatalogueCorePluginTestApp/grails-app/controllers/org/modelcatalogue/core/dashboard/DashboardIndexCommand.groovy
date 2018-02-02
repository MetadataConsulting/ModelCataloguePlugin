package org.modelcatalogue.core.dashboard

import grails.validation.Validateable
import org.modelcatalogue.core.api.ElementStatusUtils
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery

@Validateable
class DashboardIndexCommand {

    Integer max = 25
    DashboardDropdown status = DashboardDropdown.ACTIVE
    String order = 'asc'
    String sort = 'name'
    String search
    Integer offset = 0

    static constraints = {
        search nullable: true, blank: true
        offset nullable: false, min: 0
        sort nullable: false, inList: ['name', 'status', 'semanticVersion', 'lastUpdated']
        order nullable: false, inList: ['asc', 'desc']
    }

    SearchStatusQuery toSearchStatusQuery() {
        String search = search?.trim() ?: null
        new SearchStatusQuery(search: search, statusList: ElementStatusUtils.of(status))
    }

    SortQuery toSortQuery() {
        new SortQuery(order: order, sort: sort)
    }

    PaginationQuery toPaginationQuery() {
        new PaginationQuery(max: max, offset: offset)
    }
}
