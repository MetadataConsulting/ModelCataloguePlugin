package org.modelcatalogue.core

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class SortParamsUtils {
    public static final String ORDER_ASC = 'asc'
    public static final String ORDER_DESC = 'desc'
    public static final String DEFAULT_ORDER = 'asc'
    public static final String SORT_KEY = 'sort'
    public static final String ORDER_KEY = 'order'


    @CompileDynamic
    static List sort(List items, Map<String, Object> params) {
        if ( !items ) {
            return items
        }
        if ( !params.containsKey(SORT_KEY) ) {
            return items
        }
        final String sort = params[SORT_KEY]
        if ( canListBeSortedBySortProperty(items, sort) ) {
            final String order = orderFromParams(params)
            Closure sortCls = { Object a, Object b ->
                if ( order == ORDER_ASC ) {
                    return a.getAt(sort) <=> b.getAt(sort)
                }
                b.getAt(sort) <=> a.getAt(sort)
            }
            return items.sort(false, sortCls)
        }

        items
    }

    static String orderFromParams(Map<String, Object> params) {
        if ( params.containsKey(ORDER_KEY) ) {
            String paramOrder = params[ORDER_KEY]
            if ( validateOrder(paramOrder) ) {
                return paramOrder
            }
        }
        return DEFAULT_ORDER
    }

    static boolean canListBeSortedBySortProperty(List items, String sort) {
        Object firstItem = items.first()
        firstItem.hasProperty(sort)
    }

    static boolean validateOrder(String order) {
        [ORDER_ASC, ORDER_DESC].contains(order)
    }
}

