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
    DashboardStatusDropdown status = DashboardStatusDropdown.ACTIVE
    SearchScope searchScope = SearchScope.DATAMODEL
    List<SearchCatalogueElementScope> searchCatalogueElementScopes = [SearchCatalogueElementScope.ALL]
    String order = 'asc'
    String sort = 'name'
    String search
    Integer offset = 0

    static constraints = {
        dataModelId nullable: true
        metadataDomain nullable: false
        status nullable: false
        searchScope nullable: false
        search nullable: true, blank: true
        offset nullable: true, min: 0
        searchCatalogueElementScopes nullable: false, minSize: 1
        sort nullable: true, inList: ['name', 'status', 'semanticVersion', 'lastUpdated']
        order nullable: true, inList: ['asc', 'desc']
    }

    SearchQuery toSearchQuery() {
        String search = search?.trim() ?: null
        String dataModelIdStr = this.dataModelId
        if ( dataModelIdStr == 'null') {
            dataModelIdStr = null
        }
        Long dataModelId = dataModelIdStr as Long

        new SearchQuery(
                dataModelId: dataModelId,
                searchScope: searchScope,
                search: search,
                statusList: ElementStatusUtils.of(status),
                metadataDomain: metadataDomain,
                searchCatalogueElementScopeList: searchCatalogueElementScopes
        )
    }

    SortQuery toSortQuery() {
        new SortQuery(order: order, sort: sort)
    }

    PaginationQuery toPaginationQuery() {
        new PaginationQuery(max: max, offset: offset)
    }
}
