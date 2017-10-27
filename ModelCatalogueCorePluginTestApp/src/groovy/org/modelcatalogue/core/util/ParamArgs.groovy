package org.modelcatalogue.core.util

import groovy.transform.CompileStatic

@CompileStatic
class ParamArgs {
    int offset
    int max
    String sort
    String order


    Object asType(Class clazz) {
        if (clazz == Map) {
            Map<String, Object> m = [offset: offset, max: max] as Map<String, Object>
            if ( sort ) {
                m['sort'] = sort
            }
            if ( order ) {
                m['order'] = order
            }
            return m
        }
        super.asType(clazz)
    }
}
