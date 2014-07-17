package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria

class Lists {

    public static <T> ListWrapper<T> wrap(Map params, String base, ListWithTotalAndType<T> list){
        if (list instanceof ListWrapper) return list
        ListWithTotalAndTypeWrapper.create(params, base, null, list)
    }

    public static <T> ListWrapper<T> wrap(Map params, String base, String name, ListWithTotalAndType<T> list){
        if (list instanceof ListWrapper) return list
        ListWithTotalAndTypeWrapper.create(params, base, name, list)
    }

    public static <T> ListWrapper<T> wrap(Map params, Class<T> type,  String base, ListWithTotal<T> list){
        if (list instanceof ListWrapper) return list
        ListWithTotalAndTypeWrapper.create(params, base, null, list instanceof ListWithTotalAndType ? list : new ListWithTotalWrapper<T>(type, list))
    }

    public static <T> ListWrapper<T> wrap(Map params, Class<T> type, String base, String name, ListWithTotal<T> list){
        if (list instanceof ListWrapper) return list
        ListWithTotalAndTypeWrapper.create(params, base, name, list instanceof ListWithTotalAndType ? list : new ListWithTotalWrapper<T>(type, list))
    }

    public static <T> ListWrapper<T> all(Map params, Class<T> type, String base){
        all(params, type, base, (String) null)
    }

    public static <T> ListWrapper<T> all(Map params, Class<T> type, String base, String name){
        fromCriteria(params, type, base, name, {})
    }

    public static <T> ListWrapper<T> fromCriteria(Map params, Class<T> type, String base, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        fromCriteria(params, type, base, null, buildClosure)
    }

    public static <T> ListWrapper<T> fromCriteria(Map params, Class<T> type, String base, String name, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        wrap(params, base, name, DetachedListWithTotalAndType.create(params, type, buildClosure))
    }

    public static <T> ListWrapper<T> fromCriteria(Map params, String base, DetachedCriteria<T> criteria){
        fromCriteria(params, base, null, criteria)
    }

    public static <T> ListWrapper<T> fromCriteria(Map params, String base, String name, DetachedCriteria<T> criteria){
        wrap(params, base, name, DetachedListWithTotalAndType.create(params, criteria))
    }

    public static <T> ListWithTotalAndType<T> fromCriteria(Map params, Class<T> type, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        DetachedListWithTotalAndType.create(params, new DetachedCriteria<T>(type).build(buildClosure))
    }

    public static <T> ListWithTotalAndType<T> fromCriteria(Map params, DetachedCriteria<T> criteria){
        DetachedListWithTotalAndType.create(params, criteria)
    }

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
