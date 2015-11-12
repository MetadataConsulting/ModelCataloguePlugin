package org.modelcatalogue.core.elasticsearch

import groovy.util.logging.Log4j
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit
import org.modelcatalogue.core.util.ListWithTotalAndType

@Log4j
class ElasticSearchQueryList<T> implements ListWithTotalAndType<T> {

    final Map<String, Object> params
    final Class<T> type
    final SearchRequestBuilder searchRequest

    SearchResponse response

    static <T> ListWithTotalAndType<T> search(Map<String, Object> params, Class<T> type, SearchRequestBuilder searchRequest) {
        return new ElasticSearchQueryList<T>(params, type, searchRequest)
    }

    private ElasticSearchQueryList(Map<String, Object> params, Class<T> type, SearchRequestBuilder searchRequest) {
        this.params = params
        this.type = type
        this.searchRequest = searchRequest
    }

    @Override
    Class<T> getItemType() {
        return type
    }

    @Override
    Long getTotal() {
        if (!response) {
            response = initializeResponse()
        }
        return response.hits.totalHits
    }

    private SearchResponse initializeResponse() {
        try {
            if (params.offeset) {
                searchRequest.setFrom(Integer.parseInt(params.offset.toString(), 10))
            }
            if (params.max) {
                searchRequest.setSize(Integer.parseInt(params.max.toString(), 10))
            }
            searchRequest.execute().get()
        } catch (Exception e) {
            log.error("Exception searching query: ${searchRequest.toString()}")
            throw e
        }
    }

    @Override
    List<T> getItems() {
        if (!response) {
            response = initializeResponse()
        }
        return response.hits.hits.collect { SearchHit hit ->
            type.get(hit.id().toLong())
        }
    }
}
