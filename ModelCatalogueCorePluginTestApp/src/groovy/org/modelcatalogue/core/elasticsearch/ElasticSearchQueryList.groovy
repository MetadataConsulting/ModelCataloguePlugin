package org.modelcatalogue.core.elasticsearch

import groovy.util.logging.Log4j
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.index.IndexNotFoundException
import org.elasticsearch.search.SearchHit
import org.joda.time.format.ISODateTimeFormat
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.lists.JsonAwareListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.SearchParams

@Log4j
class ElasticSearchQueryList<T> implements JsonAwareListWithTotalAndType<T> {

    final SearchParams params
    final Class<T> type
    final SearchRequestBuilder searchRequest

    DataModelAclService dataModelAclService

    SearchResponse response
    List<T> items
    Long total

    static <T> ListWithTotalAndType<T> search(SearchParams params, Class<T> type, SearchRequestBuilder searchRequest, DataModelAclService dataModelAclService) {
        return new ElasticSearchQueryList<T>(params, type, searchRequest, dataModelAclService)

    }

    private ElasticSearchQueryList(SearchParams params, Class<T> type, SearchRequestBuilder searchRequest, DataModelAclService dataModelAclService) {
        this.params = params
        this.type = type
        this.searchRequest = searchRequest
        this.dataModelAclService = dataModelAclService
    }

    @Override
    Class<T> getItemType() {
        return type
    }

    @Override
    Long getTotal() {
        if (!response) {
            setResponse(initializeResponse())
        }
        this.total
    }

    void setResponse(SearchResponse searchResponse) {
        this.response = searchResponse

        items = calculateItems()
        if ( shouldUseMaxAndOffsetParamInSearchRequest() ) {
            total = response.hits.totalHits
        } else {
            total = items.size()
            items = subListItemsWithMaxAndOffset(items)
        }
    }

    List<T> subListItemsWithMaxAndOffset(List<T> items) {
        int fromIndex = subListItemsFromIndex()
        int toIndex = subListItemsToIndex(items)
        items.subList(fromIndex, toIndex)
    }

    int subListItemsToIndex(List<T> items) {
        if (isOffsetPresent() && isMaxPresent() ) {
            int offset = offset()
            int max = max()
            return Math.min( (offset + max), items.size())

        } else if ( isMaxPresent() ) {
            int max = max()
            return Math.min( max, items.size())
        }
        return items.size()
    }

    int subListItemsFromIndex() {
        return isOffsetPresent() ? offset() : 0
    }

    boolean isOffsetPresent() {
        params.paramArgs?.offset
    }

    boolean isMaxPresent() {
        params.paramArgs?.max
    }

    List<T> calculateItems() {
        List<Long> hitsIds = collectHitIds()
        collectAuthorizedObjects(hitsIds).findAll { AuthorizedObject authorizedObject ->
            authorizedObject.authorized
        }.collect { AuthorizedObject authorizedObject ->
            authorizedObject.instance
        }
    }

    List<Long> collectHitIds() {
        SearchHit[] hits = response.hits.hits
        hits.collect { SearchHit hit ->
            hit.field('entity_id')?.toLong() ?: hit.id().toLong()
        }
    }

    List<AuthorizedObject> collectAuthorizedObjects(List<Long> hitsIds) {
        hitsIds.collect { Long hitId ->
            def instance = type.get(hitId)
            boolean authorized = dataModelAclService.isAdminOrHasReadPermission(instance)
            new AuthorizedObject(instance: instance, authorized: authorized)
        }
    }

    boolean shouldUseMaxAndOffsetParamInSearchRequest() {
        // maybe we can allow for those types which
        // don't extend from CatalalogueModel. types where we don't need to check
        // if the user has ACL permissions to the dataModel
        false
    }

    void setSearchRequestForm() {
        if (shouldUseMaxAndOffsetParamInSearchRequest() && isOffsetPresent()) {
            int offset = offset()
            searchRequest.setFrom(offset)
        }
    }

    int max() {
        isMaxPresent() ? params.paramArgs?.max : 10
    }

    int offset() {
        isOffsetPresent() ? params.paramArgs?.offset : 0
    }

    void setSearchRequestMax() {
        if (shouldUseMaxAndOffsetParamInSearchRequest() && isMaxPresent()) {
            int max = max()
            searchRequest.setSize(max)
        }
    }

    private SearchResponse initializeResponse() {
        try {
            setSearchRequestForm()
            setSearchRequestMax()
            if (params.explain) {
                log.info searchRequest.toString()
            }
            searchRequest.execute().get()
        } catch (Exception e) {
            if (e.cause instanceof IndexNotFoundException) {
                log.error("Search index not found: ${e.cause.index}")
            } else {
                log.error("Exception searching query: ${searchRequest.toString()}")
            }
            throw e
        }
    }

    @Override
    List<T> getItems() {
        if (!response) {
            setResponse(initializeResponse())
        }
        this.items
    }

    Map getItemsWithScore() {
        Map  results = [:]
        if (!response) {
            try {
                response = initializeResponse()
            } catch (Exception ignored) {
                return []
            }
        }
        response.hits.hits.each { SearchHit hit ->
            results.put(type.get(hit.field('entity_id')?.toLong() ?: hit.id().toLong()), hit.score)
        }
        return results
    }

    @Override
    List<Object> getJsonItems() {
        if (!response) {
            return []
        }
        // don't forget to add field into the fetched field in the constructor if you want to use it
        if (CatalogueElement.isAssignableFrom(type)) {
            return response.hits.hits.collect { SearchHit hit ->
                readSource(hit.id(), hit.source)
            }
        }
        return items
    }

    private static Date readDate(Map<String, Object> source, String property) {
        Object value = source.get(property)
        if (!value) {
            return null
        }
        return ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(value.toString()).toDate()
    }


    private static Map<String, Object> readSource(String id, Map<String, Object> source) {
        if (!source) {
            return null
        }

        Map<String, Object> ret = [
                name: source.get('name'),
                id: id ,
                elementType: source.get('fully_qualified_type'),
                link:  source.get('link'), status: source.get('status'),
                versionNumber: source.get('version_number'),
                latestVersionId: source.get('latest_id'),
                dataModel: readSource(source.get('data_model')?.entity_id?.toString(), source.get('data_model') as  Map<String, Object>),
                classifiedName: source.get('data_model') ? "${source.get('name')} (${source.get('data_model').get('name')} ${source.get('data_model').get('semantic_version')})" : source.get('name'),
                modelCatalogueId: source.get('model_catalogue_id'),
                internalModelCatalogueId: source.get('internal_model_catalogue_id'),
                description: source.get('description'),
                ext: OrderedMap.toJsonMap(source.get('ext')),
                dateCreated: readDate(source, 'data_created'),
                versionCreated: readDate(source, 'version_created'),
                lastUpdated: readDate(source, 'last_updated'),
                minimal: true
        ]

        if (ret.elementType == DataModel.name) {
            ret.revisionNotes = source.get('revision_notes')
            ret.semanticVersion = source.get('semantic_version')
        } else if (ret.elementType == MeasurementUnit.name) {
            ret.symbol = source.get('symbol')
        } else if (ret.elementType == DataElement.name) {
            if (source.data_type) {
                ret.dataType = readSource(source.data_type.entity_id.toString(), source.data_type)
            }
        } else if (ret.elementType == PrimitiveType.name) {
            if (source.measurement_unit) {
                ret.measurementUnit = readSource(source.measurement_unit.entity_id.toString(), source.measurement_unit)
            }
        } else if (ret.elementType == ReferenceType.name) {
            if (source.data_class) {
                ret.dataClass = readSource(source.data_class.entity_id.toString(), source.data_class)
            }
        } else if (ret.elementType == EnumeratedType.name) {
            if (source.enumerated_value) {
                ret.enumerations = Enumerations.from(source.enumerated_value).toJsonMap()
            }
        }

        return ret
    }
}

class AuthorizedObject {
    def instance
    boolean authorized

}