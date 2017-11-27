package org.modelcatalogue.core.util

import groovy.transform.CompileStatic

@CompileStatic
class ParamArgs {
    int offset
    int max
    String sort
    String order

    Map toMap() {
        Map<String, Object> m = [offset: offset, max: max] as HashMap<String, Object>
        if ( sort ) {
            m['sort'] = sort
        }
        if ( order ) {
            m['order'] = order
        }
        m
    }
}
