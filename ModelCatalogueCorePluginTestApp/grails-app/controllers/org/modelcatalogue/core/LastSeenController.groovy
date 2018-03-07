package org.modelcatalogue.core

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.persistence.UserAuthenticationGormService
import org.modelcatalogue.core.security.LastSeen
import org.modelcatalogue.core.security.UserAuthentication
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery

@CompileStatic
class LastSeenController {

    static allowedMethods = [index: 'GET']

    UserAuthenticationGormService userAuthenticationGormService

    @CompileDynamic
    def index(LastSeenCommand cmd) {
        SortQuery sortQuery = cmd.toSortQuery()
        PaginationQuery paginationQuery = cmd.toPaginationQuery()

        List<LastSeen> userAuthenticationList = userAuthenticationGormService.findAllLatest(sortQuery, paginationQuery)
        Number total = userAuthenticationGormService.countDistinct()
        [
                sortQuery: sortQuery,
                paginationQuery: paginationQuery,
                userAuthenticationList: userAuthenticationList,
                total: total
        ]
    }
}
