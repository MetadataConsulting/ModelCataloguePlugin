package org.modelcatalogue.core.elasticsearch

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import grails.util.GrailsNameUtils
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.util.concurrent.EsRejectedExecutionException
import org.elasticsearch.index.VersionType
import org.elasticsearch.index.engine.VersionConflictEngineException
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.indices.IndexAlreadyExistsException
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.threadpool.ThreadPool
import org.modelcatalogue.core.*
import org.modelcatalogue.core.elasticsearch.rx.RxElastic
import org.modelcatalogue.core.rx.RxService
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Lists
import rx.Observable

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass
import static rx.Observable.from
import static rx.Observable.just

class ElasticSearchService implements SearchCatalogue {

    static transactional = false

    private static Cache<String, Map<String, Map>> mappingsCache = CacheBuilder.newBuilder().initialCapacity(20).build()

    private static final int ELEMENTS_PER_BATCH = 25
    private static final int DELAY_AFTER_BATCH = 0

    private static final String MC_PREFIX = "mc_"
    private static final String GLOBAL_PREFIX = "${MC_PREFIX}global_"
    private static final String MC_ALL_WILDCARD = "${MC_PREFIX}*"
    private static final String DATA_MODEL_INDEX = "${GLOBAL_PREFIX}data_model"
    private static final String DATA_MODEL_PREFIX = "${MC_PREFIX}data_model_"
    private static final String ORPHANED_INDEX = "${GLOBAL_PREFIX}orphaned"

    private static Map<String, Integer> CATALOGUE_ELEMENT_BOOSTS = [

            name_not_analyzed: 200,
            name: 100,
            full_version: 90,
            latest_id: 80,
            entity_id : 70,
            description: 1
    ]

    private static Set<Class> MAPPED_TYPES_IN_DATA_MODEL = [
            DataModel, Asset, DataClass, DataElement, DataType, EnumeratedType, MeasurementUnit, PrimitiveType, ReferenceType, Relationship
    ]

    GrailsApplication grailsApplication
    DataModelService dataModelService
    ElementService elementService
    RxService rxService
    Node node
    Client client

    @PostConstruct
    private void init() {
        if (grailsApplication.config.mc.search.elasticsearch.local || System.getProperty('mc.search.elasticsearch.local')) {
            Settings.Builder settingsBuilder = Settings.builder()
                .put("${ThreadPool.THREADPOOL_GROUP}${ThreadPool.Names.BULK}.queue_size", 3000)
                .put("${ThreadPool.THREADPOOL_GROUP}${ThreadPool.Names.BULK}.size", 25)
                .put('path.home', (grailsApplication.config.mc.search.elasticsearch.local ?:  System.getProperty('mc.search.elasticsearch.local')).toString())
            node = NodeBuilder.nodeBuilder()
                    .settings(settingsBuilder)
                    .local(true).node()

            client = node.client()

            log.info "Using local ElasticSearch instance in directory ${grailsApplication.config.mc.search.elasticsearch.local}"
        } else if (grailsApplication.config.mc.search.elasticsearch.host || System.getProperty('mc.search.elasticsearch.host')) {
            String host = grailsApplication.config.mc.search.elasticsearch.host ?: System.getProperty('mc.search.elasticsearch.host')
            String port = grailsApplication.config.mc.search.elasticsearch.port ?: System.getProperty('mc.search.elasticsearch.port') ?: "9300"

            Settings.Builder settingsBuilder = Settings.builder()

            if (grailsApplication.config.mc.search.elasticsearch.settings) {
                grailsApplication.config.mc.search.elasticsearch.settings(settingsBuilder)
            }


            client = TransportClient
                    .builder()
                    .settings(settingsBuilder)
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), Integer.parseInt(port, 10)))

            log.info "Using ElasticSearch instance at $host:$port"
        }

    }

    @PreDestroy
    public void cleanUp() throws Exception {
        node?.close()
        client?.close()
    }

    @Override
    ListWithTotalAndType<Relationship> search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params) {
        if (!type.searchable) {
            return Lists.emptyListWithTotalAndType(Relationship)
        }

        List<String> indicies = collectDataModelIndicies(params)
        String search = params.search

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()

        if (search != '*') {
            boolQuery.minimumNumberShouldMatch(1)
        }

        if (type) {
            boolQuery.must(QueryBuilders.termQuery('relationship_type.name', type.name))
        }

        List<String> states = []

        if (params.status) {
            states = ElementService.getStatusFromParams(params)*.toString()
        }

        List<String> types = []

        if (params.elementType) {
            String elementType = params.elementType.toString()
            if (!elementType.contains('.')) {
                GrailsClass clazz = grailsApplication.getDomainClasses().find { it.logicalPropertyName == elementType }
                if (clazz && clazz.clazz) {
                    elementType = clazz.name
                }
            }
            try {
                Class clazz = Class.forName(elementType, true, Thread.currentThread().getContextClassLoader())

                types = elementService.collectSubclasses(clazz)*.name

            } catch (ClassNotFoundException cnfe) {
                log.error "Cannot filter by class $params.elementType", cnfe
            }
        }

        switch (direction) {
            case RelationshipDirection.INCOMING:
                boolQuery.should(QueryBuilders.prefixQuery('source.name', search))
                boolQuery.should(QueryBuilders.matchQuery('source.name', search))

                if (states) {
                    boolQuery.must(QueryBuilders.termsQuery('source.status', states))
                }

                if (types) {
                    boolQuery.must(QueryBuilders.termsQuery('source.fully_qualified_type', types))
                }

                break;
            default:
                boolQuery.should(QueryBuilders.prefixQuery('destination.name', search))
                boolQuery.should(QueryBuilders.matchQuery('destination.name', search))

                if (states) {
                    boolQuery.must(QueryBuilders.termsQuery('destination.status', states))
                }

                if (types) {
                    boolQuery.must(QueryBuilders.termsQuery('destination.fully_qualified_type', types))
                }

                break;
        }

        SearchRequestBuilder request = client
                .prepareSearch(indicies as String[])
                .setTypes(getTypeName(Relationship))
                .setQuery(boolQuery)


        return ElasticSearchQueryList.search(params,Relationship, request)
    }

    @Override
    public <T> ListWithTotalAndType<T> search(Class<T> resource, Map params) {
        String search = params.search
        QueryBuilder qb
        List<String> indicies

        if (CatalogueElement.isAssignableFrom(resource)) {
            indicies = resource == DataModel ? [DATA_MODEL_INDEX] : collectDataModelIndicies(params)

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()

            if (search != '*') {
                boolQuery.minimumNumberShouldMatch(1)
            }

            if (params.status) {
                boolQuery.must(QueryBuilders.termsQuery('status', ElementService.getStatusFromParams(params)*.toString()))
            }

            if (params.contentType) {
                boolQuery.must(QueryBuilders.termsQuery('content_type', params.contentType))
            }

            CATALOGUE_ELEMENT_BOOSTS.each { String property, int boost ->
                boolQuery.should(QueryBuilders.matchQuery(property, search).boost(boost))
            }

            boolQuery.should(QueryBuilders.prefixQuery('name', search).boost(200))
            boolQuery.should(QueryBuilders.nestedQuery('ext', QueryBuilders.termQuery('ext.value', search)).boost(10))

            qb = boolQuery
        } else if (RelationshipType.isAssignableFrom(resource)) {
            indicies = [getGlobalIndexName(resource)]

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumNumberShouldMatch(1)

            if (params.status) {
                boolQuery.must(QueryBuilders.termsQuery('status', ElementService.getStatusFromParams(params)*.toString()))
            }

            CATALOGUE_ELEMENT_BOOSTS.each { String property, int boost ->
                boolQuery.should(QueryBuilders.matchQuery(property, search).boost(boost))
            }

            boolQuery.should(QueryBuilders.prefixQuery('name', search).boost(200))

            qb = boolQuery
        } else {
            indicies = [getGlobalIndexName(resource)]
            qb = QueryBuilders.queryStringQuery(search).defaultField("name")
        }

        SearchRequestBuilder request = client
                .prepareSearch(indicies as String[])
                .setFetchSource(true)
                .setTypes(collectTypes(resource) as String[])
                .setQuery(qb)


        return ElasticSearchQueryList.search(params,resource, request)
    }

    private static String getGlobalIndexName(Class resource) {
        "${GLOBAL_PREFIX}${getTypeName(resource)}"
    }



    private List<String> collectDataModelIndicies(Map params) {
        DataModelFilter filter = getOverridableDataModelFilter(params)

        if (!filter) {
            return [MC_ALL_WILDCARD] // search all by default
        }

        if (filter.unclassifiedOnly) {
            return [ORPHANED_INDEX]
        }

        if (filter.includes) {
            // excludes are ignored if there are includes
            return filter.includes.collect { getDataModelIndex(it) }
        }

        if (filter.excludes) {
            return ["${DATA_MODEL_PREFIX}*"] + filter.excludes.collect { "-${getDataModelIndex(it)}" }
        }

        throw new IllegalStateException("Unknown filter setup: $filter")
    }

    @Override
    ListWithTotalAndType<CatalogueElement> search(Map params) {
        search CatalogueElement, params
    }


    @Override
    boolean isIndexingManually() {
        return true
    }

    @Override
    Observable<Boolean> index(Object object) {
        index IndexingSession.create(), just(object)
    }

    @Override
    Observable<Boolean> index(Iterable<Object> resource) {
        index IndexingSession.create(), from(resource)
    }

    Observable<Boolean> index(IndexingSession session, Observable<Object> entities) {
        toSimpleIndexRequests(session, entities).groupBy {
          it.index
        } flatMap { documents ->
            documents.buffer(ELEMENTS_PER_BATCH)
        } flatMap {
            bulkIndex(it)
        } flatMap { bulkResponse ->
            from(bulkResponse.items)
        } map { bulkResponseItem ->
            if (bulkResponseItem.failure) {
                log.warn "Failed to index ${bulkResponseItem.type}:${bulkResponseItem.id} to ${bulkResponseItem.index}"
            } else {
                log.debug "Indexed ${bulkResponseItem.type}:${bulkResponseItem.id} to ${bulkResponseItem.index}"
            }
            SimpleIndexResponse.from(bulkResponseItem)
        } all {
           it.ok
        }
    }

    @Override
    Observable<Boolean> unindex(Object object) {
        log.debug "Unindexing $object"
        Class clazz = getEntityClass(object)
        if (DataModel.isAssignableFrom(clazz)) {
            String indexName = getDataModelIndex(object as DataModel)
            return indexExists(just(indexName)).flatMap {
                if (it.exists) {
                    return RxElastic.from(client.admin().indices().prepareDelete(indexName)).map {
                        it.acknowledged
                        log.debug "Deleted index $indexName"
                    }.onErrorReturn { error ->
                        log.debug "Exception deleting index $indexName: $error"
                        return false
                    }
                }
                return just(true)
            }
        }
        if (CatalogueElement.isAssignableFrom(clazz) || Relationship.isAssignableFrom(clazz) || RelationshipType.isAssignableFrom(clazz)) {
            Object element = object
            return from(getIndices(element)).flatMap { idx ->
                indexExists(just(idx)).flatMap { response ->
                    if (response.exists) {
                        return RxElastic.from(client.prepareDelete(idx, getTypeName(clazz), "${element.getId()}")).map {
                            log.debug "Unindexed $element from $idx"
                            it.found
                        }.onErrorReturn { error ->
                            log.debug "Exception unindexing $element: $error"
                            return false
                        }
                    }
                    return just(true)
                }
            }
        }
        return just(false)
    }

    @Override
    Observable<Boolean> unindex(Collection<Object> object) {
        return from(object).flatMap {
            unindex(it)
        }.all {
            it
        }
    }

    @Override
    Observable<Boolean> reindex() {
        IndexingSession session = IndexingSession.create()

        Observable<Object> elements = rxService.from(DataModel.where{}, sort: 'lastUpdated', order: 'desc', true, ELEMENTS_PER_BATCH, DELAY_AFTER_BATCH
        ) flatMap {
            return getDataModelWithDeclaredElements(it)
        } concatWith (
            rxService.from(dataModelService.classified(CatalogueElement, DataModelFilter.create(true)), true, ELEMENTS_PER_BATCH, DELAY_AFTER_BATCH)
        ) concatWith (
            rxService.from(RelationshipType.where {})
        )

        return RxElastic.from(client.admin().indices().prepareDelete("${MC_PREFIX}*")).map { it.acknowledged }.concatWith(index(session, elements))
            .doOnError {
                log.error "Exception reindexing catalogue: ${it.getClass()}", it
            }

    }

    Map<String, Map> getMapping(Class clazz) {
        getMapping(clazz, clazz)
    }

    Map<String, Map> getMapping(Class clazz, Class implementation) {
        mappingsCache.get("$clazz=>$implementation") {
            getMappingInternal(clazz, implementation)
        }
    }

    Map<String, Map> getMappingInternal(Class clazz, Class implementation) {
        ElasticSearchService service = this

        if (!clazz) {
            return [:]
        }


        String typeName = getTypeName(clazz)
        Map<String, Map> mapping = [(typeName): [:]]

        if (clazz.superclass && clazz.superclass != Object) {
            merge mapping[typeName], service.getMapping(clazz.superclass, implementation)[getTypeName(clazz.superclass)]
        }

        URL url = ElasticSearchService.getResource("${clazz.simpleName}.mapping.json")


        if (url) {
            File mappingFile = new File(url.toURI())
            Map parsed = new JsonSlurper().parse(mappingFile) as Map
            parsed.toString() // initialize lazy map
            Map parsedMapping = parsed[typeName] as Map
            if (!parsedMapping) {
                log.warn "the mapping does not contain expected root entry ${typeName}"
            } else {
                merge mapping[typeName], parsedMapping
            }
        }

        if (clazz == DataElement) {
            // data type is embedded into data element
            mapping[typeName].properties.data_type = getMapping(DataType).data_type
        } else if (clazz in [PrimitiveType, DataType]) {
            // measurement unit is embedded in primitive type
            mapping[typeName].properties.measurement_unit = getMapping(MeasurementUnit).measurement_unit
        } else if (clazz  in [ReferenceType, DataType]) {
            // data class is embedded in refrence type
            mapping[typeName].properties.data_class = getMapping(DataClass).data_class
        } else if (clazz == Relationship) {
            // source and destination combines all available catalogue element mappings
            // relationship copies relationship type mapping
            mapping[typeName].properties.relationship_type = getMapping(RelationshipType).relationship_type

            Map catalogueElementMappingCombined = combineMappingForAllCatalogueElements()

            mapping[typeName].properties.source = catalogueElementMappingCombined.catalogue_element
            mapping[typeName].properties.destination = catalogueElementMappingCombined.catalogue_element
        } else if (clazz == CatalogueElement && implementation != DataModel) {
            // source and destination combines all available catalogue element mappings
            // relationship copies relationship type mapping
            mapping[typeName].properties.data_model = getMapping(DataModel).data_model
        }

        return mapping

    }


    private Observable<SimpleIndexRequest> toSimpleIndexRequests(IndexingSession session, Observable<Object> entites) {
        entites.groupBy {
            // XXX: shouldn't the change in data model trigger reindexing everything in the data model?
            Class clazz = getEntityClass(it)
            if (CatalogueElement.isAssignableFrom(clazz)) {
                return CatalogueElement
            }

            if (RelationshipType.isAssignableFrom(clazz)) {
                return RelationshipType
            }

            if (Relationship.isAssignableFrom(clazz)) {
                return Relationship
            }
            return clazz
        } flatMap { group ->
            if (group.key == CatalogueElement) {
                return group.buffer(ELEMENTS_PER_BATCH).flatMap { elements ->
                    from(elements).concatWith(rxService.from(Relationship.where { (source in elements || destination in elements) && relationshipType.searchable == true }, true, ELEMENTS_PER_BATCH, DELAY_AFTER_BATCH))
                }
            }
            if (group.key == Relationship) {
                return group.flatMap { entity -> getRelationshipWithSourceAndDestination(session, entity as Relationship) }
            }
            if (group.key == RelationshipType) {
                return group
            }
            throw new UnsupportedOperationException("Not Yet Implemented for $group.key")
        } flatMap { entity ->
            Class clazz = getEntityClass(entity)
            from(getIndices(entity)).map { index ->
                new SimpleIndexRequest(index, session.getDocument(entity), (CatalogueElement.isAssignableFrom(clazz) || Relationship.isAssignableFrom(clazz)) ? MAPPED_TYPES_IN_DATA_MODEL : [clazz] as Set)
            }
        }
    }

    private Observable<CatalogueElement> getDataModelWithDeclaredElements(DataModel element) {
        just(element as CatalogueElement).concatWith(rxService.from(CatalogueElement.where { dataModel == element }, true, ELEMENTS_PER_BATCH, DELAY_AFTER_BATCH))
    }

    private static Observable<Document> getRelationshipWithSourceAndDestination(IndexingSession session, Relationship rel) {
        just(rel, rel.source, rel.destination).map { session.getDocument(it) }
    }

    private Observable<BulkResponse> bulkIndex(List<SimpleIndexRequest> documents) {
        SimpleIndexRequest first = documents.first()
        ensureIndexExists(just(first.index), first.mappedClasses).flatMap {
            RxElastic.from(buildBulkIndexRequest(documents))
                .flatMap {
                for (BulkItemResponse response in it.items) {
                    if (response.failed) {
                        if (response.failure.cause instanceof VersionConflictEngineException) {
                            // ignore and keep the latest
                            continue
                        }
                        return Observable.error(new RuntimeException("There were error indexing at least of one item from the batch: $response.type#$response.id@$response.index", response.failure.cause))
                    }
                }
                return just(it)
            }
        }
        .retryWhen(RxService.withDelay(RxElastic.DEFAULT_RETRIES, RxElastic.DEFAULT_DELAY, [EsRejectedExecutionException] as Set))
    }

    private BulkRequestBuilder buildBulkIndexRequest(List<SimpleIndexRequest> indexRequests) {
        BulkRequestBuilder bulkRequest = client.prepareBulk()
        for (SimpleIndexRequest indexRequest in indexRequests) {
            bulkRequest.add(client
                .prepareIndex(indexRequest.index, indexRequest.document.type, indexRequest.document.id)
                .setVersion(indexRequest.document.version ?: 1L)
                .setVersionType(VersionType.EXTERNAL_GTE)
                .setSource(indexRequest.document.payload)
            )
        }
        return bulkRequest
    }


    private Observable<SimpleIndicesExistsResponse> indexExists(Observable<String> indices) {
        return indices.flatMap { index ->
            return RxElastic.from(client.admin().indices().prepareExists(index)).map {
                return new SimpleIndicesExistsResponse(exists: it.exists, index: index)
            }
        }
    }

    private Observable<String> ensureIndexExists(Observable<String> indices, Iterable<Class> supportedTypes) {
        return indexExists(indices).flatMap { response ->
            if (response.exists) {
                return just(response.index)
            }
            CreateIndexRequestBuilder request = client.admin()
                    .indices()
                    .prepareCreate(response.index)

            for (Class type in supportedTypes) {
                request.addMapping(getTypeName(type), getMapping(type))
            }

            RxElastic.from(request).map {
                response.index
            } .onErrorReturn {
                if (it instanceof IndexAlreadyExistsException || it.cause instanceof IndexAlreadyExistsException) {
                    return response.index
                }
                throw it
            }
        }
    }



    protected static List<String> getIndices(object) {
        Class clazz = getEntityClass(object)
        if (DataModel.isAssignableFrom(clazz)) {
            return [DATA_MODEL_INDEX, getDataModelIndex(object as DataModel)]
        }

        if (CatalogueElement.isAssignableFrom(clazz)) {
            CatalogueElement element = object as CatalogueElement
            if (element.dataModel) {
                return [getDataModelIndex(element.dataModel)]
            }
            return [ORPHANED_INDEX]
        }

        if (RelationshipType.isAssignableFrom(clazz)) {
            return [getGlobalIndexName(clazz)]
        }

        if (Relationship.isAssignableFrom(clazz)) {
            Relationship rel = object as Relationship
            return (getIndices(rel.source) + getIndices(rel.destination)).unique()
        }

        throw new UnsupportedOperationException("Not Yet Implemented for $object")
    }

    protected static String getDataModelIndex(Long id) {
        "${DATA_MODEL_PREFIX}${id}"
    }

    protected static String getDataModelIndex(DataModel dataModel) {
        getDataModelIndex(dataModel.getId())
    }

    private Map combineMappingForAllCatalogueElements() {
        elementService.collectSubclasses(CatalogueElement).inject([catalogue_element: [:]] as Map) { Map collector, Class type ->
            merge collector.catalogue_element, getMapping(type)[getTypeName(type)]
            collector
        } as Map
    }


    static String getTypeName(Class clazz) {
        CatalogueElement.fixResourceName(GrailsNameUtils.getNaturalName(clazz.simpleName)).replaceAll(/\s/, '_').toLowerCase()
    }

    private static Map<String, Map> merge(Map<String, Map> current, Map<String, Map> fromSuper) {
        if (current == null) {
            if (fromSuper == null) {
                return [:]
            }
            return new LinkedHashMap<String, Map>(fromSuper)
        }
        for (Map.Entry<String, Map> entry in fromSuper) {
            if (current.containsKey(entry.key)) {
                current[entry.key].putAll entry.value
            } else {
                current[entry.key] = entry.value
            }
        }
        return current
    }

    private DataModelFilter getOverridableDataModelFilter(Map params) {
        if (params.dataModel) {
            DataModel dataModel = DataModel.get(params.long('dataModel'))
            if (dataModel) {
                return DataModelFilter.includes(dataModel).withImports()
            }
        }
        dataModelService.dataModelFilter.withImports()
    }

    List<String> collectTypes(Class<?> resource) {
        elementService.collectSubclasses(resource).collect { getTypeName(it) }
    }
}


