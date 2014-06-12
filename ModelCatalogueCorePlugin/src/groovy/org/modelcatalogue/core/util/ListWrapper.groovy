package org.modelcatalogue.core.util
/**
 * Wrapper used for easier marshalling of relations result lists
 */
abstract class ListWrapper<T> {
    String base
    String next
    String previous
    Class<T> itemType
    String sort
    String order
    int total
    int page
    int offset
    List<T> items
    List<Map<String, String>> availableReports = []


    static Map<String, String> nextAndPreviousLinks(Map params, String baseLink, Long total) {
        def link = "${baseLink}?"
        if (params.max) {
            link += "max=${params.max}"
        }
        if (params.sort) {
            link += "&sort=${params.sort}"
        }
        if (params.status) {
            link += "&status=${params.status}"
        }
        if (params.order) {
            link += "&order=${params.order}"
        }
        if (params.key) {
            link += "&key=${params.key}"
        }
        if (params.keyOne) {
            link += "&keyOne=${params.keyOne}"
        }
        if (params.keyTwo) {
            link += "&keyTwo=${params.keyTwo}"
        }
        if (params.toplevel) {
            link += "&toplevel=${params.toplevel}"
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
