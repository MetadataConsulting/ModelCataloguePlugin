package org.modelcatalogue.core.util

import groovy.transform.CompileStatic

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
}
