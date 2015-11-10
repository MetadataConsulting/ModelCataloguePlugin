package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists

class SearchController extends AbstractRestfulController<CatalogueElement>{

    static responseFormats = ['json', 'xml', 'xlsx']

    SearchController() {
        super(CatalogueElement, true)
    }

    def index(Integer max){
        setSafeMax(max)

        ListWithTotal<CatalogueElement> results =  modelCatalogueSearchService.search(params)

        if(results.errors){
            respond results
            return
        }

        def baseLink = "/search/?search=${params.search.encodeAsURL()}"

        int total = results.total
        def links = Lists.nextAndPreviousLinks(params, baseLink, total)
        Elements elements =  new Elements(
                base: baseLink,
                total: total,
                items: results.items,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10,
                sort: params.sort,
                order: params.order,
                itemType: CatalogueElement
        )

        respond elements

    }

    protected setSafeMax(Integer max) {
        withFormat {
            json {
                params.max = Math.min(max ?: 10, 100)
            }
            xlsx {
                params.max = Math.min(max ?: 10000, 10000)
            }
        }

    }

}
