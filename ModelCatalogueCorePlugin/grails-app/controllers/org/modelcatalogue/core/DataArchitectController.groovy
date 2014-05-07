package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper

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
        def links = ListWrapper.nextAndPreviousLinks(params, "/dataArchitect/uninstantiatedDataElements", total)
        Elements elements =  new Elements(
                total: total,
                items: results.results,
                previous: links.previous,
                itemType: DataElement,
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
        def links = ListWrapper.nextAndPreviousLinks(params, "/dataArchitect/metadataKeyCheck", total)
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

}
