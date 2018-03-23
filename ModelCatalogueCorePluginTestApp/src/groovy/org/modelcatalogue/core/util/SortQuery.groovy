package org.modelcatalogue.core.util

import groovy.transform.CompileStatic

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
}
