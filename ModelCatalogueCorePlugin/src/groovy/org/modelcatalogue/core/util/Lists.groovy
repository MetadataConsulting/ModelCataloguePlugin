package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria

class Lists {

    static <T> ListWrapper<T> wrap(Map params, String base, String name = null, ListWithTotalAndType<T> list){
        if (list instanceof ListWrapper) return list
        ListWithTotalAndTypeWrapper.create(params, base, name, list)
    }

    static <T> ListWrapper<T> wrap(Map params, Class<T> type, String base, String name = null, ListWithTotal<T> list){
        if (list instanceof ListWrapper) return list
        ListWithTotalAndTypeWrapper.create(params, base, name, list instanceof ListWithTotalAndType ? list : new ListWithTotalWrapper<T>(type, list))
    }

    static <T> ListWrapper<T> all(Map params, Class<T> type, String base, String name = null){
        fromCriteria(params, type, base, name, {})
    }

    //>-- not using default parameters, because @DelegatesTo is failing
    static <T> ListWrapper<T> fromCriteria(Map params, Class<T> type, String base, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        fromCriteria(params, type, base, null, buildClosure)
    }

    static <T> ListWrapper<T> fromCriteria(Map params, Class<T> type, String base, String name, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        wrap(params, base, name, DetachedListWithTotalAndType.create(params, type, buildClosure))
    }
    //<-- not using default parameters, because @DelegatesTo is failing

    static <T> ListWrapper<T> fromCriteria(Map params, String base, String name = null, DetachedCriteria<T> criteria){
        wrap(params, base, name, DetachedListWithTotalAndType.create(params, criteria))
    }

    static <T> ListWithTotalAndType<T> fromCriteria(Map params, Class<T> type, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        DetachedListWithTotalAndType.create(params, new DetachedCriteria<T>(type).build(buildClosure))
    }

    static <T> ListWithTotalAndType<T> fromCriteria(Map params, DetachedCriteria<T> criteria){
        DetachedListWithTotalAndType.create(params, criteria)
    }


    //>-- not providing wrapper variant because it should be used in service classes
    static <T> ListWithTotalAndType<T> fromQuery(Map params, Class<T> type, String query, Map<String, Object> arguments = [:]){
        QueryListWithTotalAndType.create(params, type, query, arguments)
    }

    static <T> ListWithTotalAndType<T> fromQuery(Map params, Class<T> type, String listQuery, String countQuery, Map<String, Object> arguments = [:]){
        QueryListWithTotalAndType.create(params, type, listQuery, countQuery, arguments)
    }
    //<-- not providing wrapper variant because it should be used in service classes



    static Map<String, String> nextAndPreviousLinks(Map params, String baseLink, Long total) {
        def link = baseLink.contains('?') ? "${baseLink}&" : "${baseLink}?"
        if (params.max) {
            link += "max=${params.max ?: 10}"
        }
        params.each { String k, Object v ->
            if (v && !(k in ['offset', 'max', 'type', 'action', 'controller', 'id']) && !(baseLink =~ /[\?&]${k}=/)) {
                link += "&$k=$v"
            }
        }

        def nextLink = ""
        def previousLink = ""
        if (params?.max && params.max < total) {
            def offset = (params?.offset) ? params?.offset?.toInteger() : 0
            def prev = offset - params?.max
            def next = offset + params?.max
            if (next < total) {
                nextLink = "${link}&offset=${next}"
            }
            if (prev >= 0) {
                previousLink = "${link}&offset=${prev}"
            }
        }
        [
                next: nextLink,
                previous: previousLink
        ]
    }
}
