package org.modelcatalogue.core.dashboard

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery
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
        if  ( cmd.hasErrors() ) {
            flash.error = messageSource.getMessage('dashboard.params.notvalid', [] as Object[], 'Invalid Parameters', request.locale)
            return
        }

        SearchQuery query = cmd.toSearchQuery()
        SortQuery sortQuery = cmd.toSortQuery()
        PaginationQuery paginationQuery = cmd.toPaginationQuery()

        CatalogueElementSearchResult catalogueElementSearchResult =
                dashboardService.search(query, sortQuery, paginationQuery)
        List catalogueElementList = catalogueElementSearchResult?.viewModels ?: []
        List<IdName> dataModelList = dashboardService.findAllDataModel()
        int total = catalogueElementSearchResult?.total ?: 0
        [
                dataModelList: dataModelList,
                metadataDomain: cmd.metadataDomain,
                metadataDomainList: dashboardService.metadataDomainList(),
                sortQuery: sortQuery,
                paginationQuery: paginationQuery,
                search: cmd.search,
                searchScope: cmd.searchScope,
                status: cmd.status,
                catalogueElementList: catalogueElementList,
                total: total,
                serverUrl: serverUrl,
                dataModelId: query.dataModelId
        ]
    }

}
