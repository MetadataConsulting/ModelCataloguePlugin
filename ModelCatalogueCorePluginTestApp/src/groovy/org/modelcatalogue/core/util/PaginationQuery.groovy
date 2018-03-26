package org.modelcatalogue.core.util

import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@CompileStatic
class PaginationQuery {
    Integer max
    Integer offset

    Map toMap() {
        if ( max  != null && offset != null ) {
            return [max: max, offset: offset]
        }
        Collections.emptyMap()
    }

    static PaginationQuery of(GrailsParameterMap params) {
        new PaginationQuery(max: params.int('max', 10), offset: params.int('offset', 0))
    }

    String toSQL() {
        "LIMIT ${offset}, ${max}"
    }
}
