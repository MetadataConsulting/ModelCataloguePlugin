package org.modelcatalogue.core.elasticsearch

import groovy.util.logging.Log4j
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.JsonAwareListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndType

@Log4j
class ElasticSearchQueryList<T> implements JsonAwareListWithTotalAndType<T> {

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

        if (CatalogueElement.isAssignableFrom(type)) {
            searchRequest.addFields('name', '_id', 'fully_qualified_type','link','status','version_number','latest_id', 'data_model', 'model_catalogue_id')
        }
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
            if (params.offset) {
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

    @Override
    List<Object> getJsonItems() {
        // don't forget to add field into the fetched field in the constructor if you want to use it
        if (CatalogueElement.isAssignableFrom(type)) {
            return response.hits.hits.collect { SearchHit hit ->
                [
                        name: hit.field('name')?.value(),
                        id: hit.id(),
                        elementType: hit.field('fully_qualified_type')?.value(),
                        link:  hit.field('link'), status: hit.field('status')?.value(),
                        versionNumber: hit.field('version_number')?.value(),
                        latestVersionId: hit.field('latest_id')?.value(),
                        classifiedName: hit.field('data_model')?.value() ? "${hit.field('name').value()} (${hit.field('data_model').value()})" : hit.field('name').value(),
                        modelCatalogueId: hit.field('model_catalogue_id')?.value()
                ]
            }
        }
        return items
    }
}
