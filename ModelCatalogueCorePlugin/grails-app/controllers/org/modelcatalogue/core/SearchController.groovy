package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper

class SearchController {

    static responseFormats = ['json', 'xml', 'xlsx']

    def modelCatalogueSearchService

    def index(Integer max){
        setSafeMax(max)
        def results =  modelCatalogueSearchService.search(params)
        if(results.errors){
            respond results
            return
        }

        def total = (results.total)?results.total.intValue():0
        def links = ListWrapper.nextAndPreviousLinks(params, "/search/${params.search}", total)
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
