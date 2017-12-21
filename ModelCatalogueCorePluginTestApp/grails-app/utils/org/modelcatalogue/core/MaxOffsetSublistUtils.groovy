package org.modelcatalogue.core

import groovy.transform.CompileStatic

@CompileStatic
class MaxOffsetSublistUtils {

    public static final String OFFSET_KEY = 'offset'
    public static final String MAX_KEY = 'max'

    static List subList(List items, Map<String, Object> params) {
        int fromIndex = subListItemsFromIndex(params)
        int toIndex = subListItemsToIndex(items, params)
        items.subList(fromIndex, toIndex)
    }

    static int subListItemsToIndex(List items, Map<String, Object> params) {
        if (params.containsKey(OFFSET_KEY) && params.containsKey('max') ) {
            int offset = params[OFFSET_KEY] as int
            int max = params[MAX_KEY] as int
            return Math.min( (offset + max), items.size())

        } else if ( params.containsKey(MAX_KEY) ) {
            int max = params[MAX_KEY] as int
            return Math.min( max, items.size())
        }
        items.size()
    }

    static int subListItemsFromIndex(Map<String, Object> params) {
        params.containsKey(OFFSET_KEY) ? params[OFFSET_KEY] as int : 0
    }
}
