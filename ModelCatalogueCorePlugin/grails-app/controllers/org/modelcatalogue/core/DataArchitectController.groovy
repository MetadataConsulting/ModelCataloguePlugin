package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements

class DataArchitectController {

    static responseFormats = ['json', 'xml', 'xlsx']

    def dataArchitectService

    def index(){}

    def uninstantiatedDataElements(Integer max){
        setSafeMax(max)
        def results =  dataArchitectService.uninstantiatedDataElements(params)
        if(results.errors){
            respond results
            return
        }

        def total = (results.totalCount)?results.totalCount:0
        def links = nextAndPreviousLinks("/dataArchitect/uninstantiatedDataElements", total)
        Elements elements =  new Elements(
                total: total,
                items: results.results,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10
        )

        respond elements
    }


    def metadataKeyCheck(Integer max){
        setSafeMax(max)
        def results =  dataArchitectService.metadataKeyCheck(params)
        if(results.errors){
            respond results
            return
        }

        def total = (results.totalCount)?results.totalCount:0
        def links = nextAndPreviousLinks("/dataArchitect/metadataKeyCheck", total)
        Elements elements =  new Elements(
                total: total,
                items: results.results,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10
        )

        respond elements
    }


    protected setSafeMax(Integer max) {
        withFormat {
            json {
                params.max = Math.min(max ?: 10, 100)
            }
            xml {
                params.max = Math.min(max ?: 10, 100)
            }
            xlsx {
                params.max = Math.min(max ?: 10000, 10000)
            }
        }

    }


//copied and pasted
    protected Map<String, String> nextAndPreviousLinks(String baseLink, Integer total) {
        def link = "${baseLink}?"
        if (params.max) {
            link += "max=${params.max}"
        }
        if (params.sort) {
            link += "&sort=${params.sort}"
        }
        if (params.order) {
            link += "&order=${params.order}"
        }
        if (params.key) {
            link += "&key=${params.key}"
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
