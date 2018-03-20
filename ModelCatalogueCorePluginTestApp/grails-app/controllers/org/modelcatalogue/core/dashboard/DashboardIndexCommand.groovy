package org.modelcatalogue.core.dashboard

import grails.validation.Validateable
import org.modelcatalogue.core.api.ElementStatusUtils
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery

@Validateable
class DashboardIndexCommand {

    String dataModelId
    Integer max = 10
    MetadataDomain metadataDomain = MetadataDomain.DATA_MODEL
    DashboardDropdown status = DashboardDropdown.ACTIVE
    String order = 'asc'
    String sort = 'name'
    String search
    Boolean searchWithWhitespace = false // whether to search with whitespace around the query
    Integer offset = 0

    static constraints = {
        dataModelId nullable: true
        metadataDomain nullable: false
        status nullable: false
        search nullable: true, blank: true
        offset nullable: true, min: 0
        sort nullable: true, inList: ['name', 'status', 'semanticVersion', 'lastUpdated']
        order nullable: true, inList: ['asc', 'desc']
    }

    SearchStatusQuery toSearchStatusQuery() {
        String search = search?.trim() ?: null
        new SearchStatusQuery(search: search, statusList: ElementStatusUtils.of(status), searchWithWhitespace: searchWithWhitespace)
    }

    SortQuery toSortQuery() {
        new SortQuery(order: order, sort: sort)
    }

    PaginationQuery toPaginationQuery() {
        new PaginationQuery(max: max, offset: offset)
    }
}
