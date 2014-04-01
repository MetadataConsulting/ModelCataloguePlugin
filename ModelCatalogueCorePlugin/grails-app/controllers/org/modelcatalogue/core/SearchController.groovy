package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements

class SearchController {

    static responseFormats = ['json', 'xml']

    def modelCatalogueSearchService

    def index(Integer max){
        params.max = Math.min(max ?: 10, 100)
        def results =  modelCatalogueSearchService.search(params)
        if(results.errors){
            respond results
            return
        }

            def total = (results.total)?results.total.intValue():0
            def links = nextAndPreviousLinks("/search/", total)
        Elements elements =  new Elements(
                    total: total,
                    items: results.searchResults,
                    previous: links.previous,
                    next: links.next,
                    offset: params.int('offset') ?: 0,
                    page: params.int('max') ?: 10
            )

        respond elements

    }


//copied and pasted
    protected Map<String, String> nextAndPreviousLinks(String baseLink, Integer total) {
        def link = "${baseLink}${params.search}?"
        if (params.max) {
            link += "max=${params.max}"
        }
        if (params.sort) {
            link += "&sort=${params.sort}"
        }
        if (params.order) {
            link += "&order=${params.order}"
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
