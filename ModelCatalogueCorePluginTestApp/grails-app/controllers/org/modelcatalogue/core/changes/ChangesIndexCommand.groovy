package org.modelcatalogue.core.changes

import grails.validation.Validateable
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery

@Validateable
class ChangesIndexCommand {
    Integer offset = 0
    Integer max = 50
    Long dataModelId
    String order = 'desc'
    String sort = 'dateCreated'

    PaginationQuery toPaginationQuery() {
        new PaginationQuery(max: max, offset: offset)
    }

    SortQuery toSortQuery() {
        new SortQuery(order: order, sort: sort)
    }

}
