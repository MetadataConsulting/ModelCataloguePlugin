package org.modelcatalogue.core.util

import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@CompileStatic
class SortQuery {
    String sort
    String order

    Map toMap() {
        if ( sort  != null && order != null ) {
            return [sort: sort, order: order]
        }
        Collections.emptyMap()
    }

    static SortQuery of(GrailsParameterMap params) {
        String sort = params.sort as String
        if ( sort == 'dateCreated' ) {
            sort = 'date_created'
        }
        new SortQuery(sort: sort, order: params.order as String)
    }

    String toSQL() {
        "order by $sort $order"
    }
}
