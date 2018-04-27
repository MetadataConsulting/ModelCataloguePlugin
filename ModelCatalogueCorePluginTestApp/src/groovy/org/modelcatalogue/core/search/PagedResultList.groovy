package org.modelcatalogue.core.search

import groovy.transform.CompileStatic

@CompileStatic
class PagedResultList {
    List items
    int total

    int getTotalCount() {
        total
    }
}
