package org.modelcatalogue.core.util

import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@CompileStatic
class PaginationQuery {

    public static final int DEFAULT_MAX = 10
    Integer max
    Integer offset = 0

    Map<String, Object> toMap() {
        if ( max  != null || offset != null ) {
            Map m = [:]
            if ( max != null) {
                m['max'] = max as int
            }
            if ( offset != null) {
                m['offset'] = offset as int
            } else {
                m['offset'] = 0
            }
            return m as Map<String, Object>
        }
        Collections.emptyMap()
    }

    static PaginationQuery of(GrailsParameterMap params) {
        new PaginationQuery(max: params.int('max', DEFAULT_MAX), offset: params.int('offset', 0))
    }

    String toSQL() {
        "LIMIT ${offset}, ${max}"
    }
}
