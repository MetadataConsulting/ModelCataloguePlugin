package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableSet
import grails.util.GrailsNameUtils
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.util.concurrent.EsRejectedExecutionException
import org.elasticsearch.index.IndexNotFoundException
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
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.cache.CacheService
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


    private static final int ELEMENTS_PER_BATCH = readFromEnv('MC_ES_ELEMENTS_PER_BATCH', 10)
    private static final int DELAY_AFTER_BATCH = readFromEnv('MC_ES_DELAY_AFTER_BATCH', 25)

    private static int readFromEnv(String envName, int defaultValue) {
        int ret = defaultValue
        String value = System.getenv(envName)
        if (value) {
            try {
                ret = Integer.parseInt(value, 10)
            } catch (NumberFormatException e) {
                e.printStackTrace()
            }
        }
        return ret
    }

    private static final String ENV_MC_ES_PREFIX = 'MC_INDEX_PREFIX'

    private static final String MC_PREFIX = "${System.getenv(ENV_MC_ES_PREFIX) ?:  System.getProperty(ENV_MC_ES_PREFIX) ?: ''}mc_"
    private static final String GLOBAL_PREFIX = "${MC_PREFIX}global_"
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

    GrailsApplication grailsApplication
    DataModelService dataModelService
    ElementService elementService
    SecurityService modelCatalogueSecurityService
    RxService rxService
    Node node
    Client client

    @PostConstruct
    private void init() {
        if (grailsApplication.config.mc.search.elasticsearch.host || System.getProperty('mc.search.elasticsearch.host')) {
            initRemoteClient()
        } else if (grailsApplication.config.mc.search.elasticsearch.local || System.getProperty('mc.search.elasticsearch.local')) {
            initLocalClient()
        }

    }

    protected void initLocalClient() {
        Settings.Builder settingsBuilder = Settings.builder()
                                                   .put("${ThreadPool.THREADPOOL_GROUP}${ThreadPool.Names.BULK}.queue_size", 3000)
                                                   .put("${ThreadPool.THREADPOOL_GROUP}${ThreadPool.Names.BULK}.size", 25)
                                                   .put("action.auto_create_index", false)
                                                   .put("index.mapper.dynamic", false)
                                                   .put('path.home', (grailsApplication.config.mc.search.elasticsearch.local ?: System.getProperty('mc.search.elasticsearch.local')).toString())
        node = NodeBuilder.nodeBuilder()
                          .settings(settingsBuilder)
                          .local(true).node()

        client = node.client()
        log.info "Using local ElasticSearch instance in directory ${grailsApplication.config.mc.search.elasticsearch.local}"
    }

    protected void initRemoteClient() {
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
            states = ElementService.getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER'))*.toString()
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
                boolQuery.must(QueryBuilders.termsQuery('destination.entity_id', element.id?.toString()))

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
                boolQuery.must(QueryBuilders.termsQuery('source.entity_id', element.id?.toString()))

                if (states) {
                    boolQuery.must(QueryBuilders.termsQuery('destination.status', states))
                }

                if (types) {
                    boolQuery.must(QueryBuilders.termsQuery('destination.fully_qualified_type', types))
                }

                break;
        }

        List<String> indicies = collectDataModelIndicies(params, [Relationship])

        SearchRequestBuilder request = client
                .prepareSearch(indicies as String[])
                .setTypes(getTypeName(Relationship))
                .setIndicesOptions(IndicesOptions.lenientExpandOpen())
                .setQuery(boolQuery)


        return ElasticSearchQueryList.search(params,Relationship, request)
    }

    @Override
    public <T> ListWithTotalAndType<T> search(Class<T> resource, Map params) {
        String search = params.search
        QueryBuilder qb
        List<String> indicies

        if (CatalogueElement.isAssignableFrom(resource)) {
            indicies = resource == DataModel ? [getGlobalIndexName(DataModel)] : collectDataModelIndicies(params, elementService.collectSubclasses(resource))

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()

            if (search != '*') {
                boolQuery.minimumNumberShouldMatch(1)
            }

            if (params.status) {
                boolQuery.must(QueryBuilders.termsQuery('status', ElementService.getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER'))*.toString()))
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

            CATALOGUE_ELEMENT_BOOSTS.each { String property, int boost ->
                boolQuery.should(QueryBuilders.matchQuery(property, search).boost(boost))
            }

            boolQuery.should(QueryBuilders.prefixQuery('name', search).boost(200))

            qb = boolQuery
        } else if (DataModelPolicy.isAssignableFrom(resource)) {
            indicies = [getGlobalIndexName(resource)]

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumNumberShouldMatch(1)

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
                .setIndicesOptions(IndicesOptions.lenientExpandOpen())
                .setQuery(qb)


        return ElasticSearchQueryList.search(params,resource, request)
    }

    protected static String getGlobalIndexName(Class resource) {
        "${GLOBAL_PREFIX}${getTypeName(resource)}"
    }

    protected static String getOrphanedIndexName(Class resource) {
        "${ORPHANED_INDEX}_${getTypeName(resource)}"
    }


    private List<String> collectDataModelIndicies(Map params, List<Class> types) {
        DataModelFilter filter = getOverridableDataModelFilter(params)

        if (!filter) {
            return types.collect { ElasticSearchService.getGlobalIndexName(it) }
        }

        if (filter.unclassifiedOnly) {
            return types.collect { ElasticSearchService.getOrphanedIndexName(it) }
        }

        if (filter.includes) {
            // excludes are ignored if there are includes
            return filter.includes.collect { types.collect { type -> ElasticSearchService.getDataModelIndex(it, getTypeName(type)) } }.flatten()
        }

        if (filter.excludes) {
            return ["${DATA_MODEL_PREFIX}*"] + filter.excludes.collect { types.collect { type -> ElasticSearchService.getDataModelIndex(it, getTypeName(type)) } }.flatten().collect {"-${it}"}
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
        toSimpleIndexRequests(session, entities).buffer(ELEMENTS_PER_BATCH).flatMap {
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
        IndexingSession session = IndexingSession.create()
        if (DataModel.isAssignableFrom(clazz)) {
            String indexName = getDataModelIndex(object as DataModel, clazz)
            return indexExists(session, just(indexName)).flatMap {
                if (it.exists) {
                    return RxElastic.from(client.admin().indices().prepareDelete(indexName)).map {
                        log.info "Deleted index $indexName"
                        session.indexExist(indexName, false)
                        return it.acknowledged
                    }.onErrorReturn { error ->
                        log.warn "Exception deleting index $indexName: $error"
                        return false
                    }.concatWith(unindexInternal(session, object))
                }
                return just(true)
            }
        }
        if (CatalogueElement.isAssignableFrom(clazz) || Relationship.isAssignableFrom(clazz) || RelationshipType.isAssignableFrom(clazz)) {
            unindexInternal(session, object)

        }
        return just(false)
    }

    private Observable<Boolean> unindexInternal(IndexingSession session, Object element) {
        return from(getIndices(element)).flatMap { idx ->
            indexExists(session, just(idx)).flatMap { response ->
                if (response.exists) {
                    return RxElastic.from(client.prepareDelete(idx, getTypeName(getEntityClass(element)), "${element.getId()}")).map {
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

    @Override
    Observable<Boolean> unindex(Collection<Object> object) {
        return from(object).flatMap {
            unindex(it)
        }.all {
            it
        }
    }

    @Override
    Observable<Boolean> reindex(boolean soft) {
        IndexingSession session = IndexingSession.create()

        Observable<Object> elements = rxService.from(DataModel.where{ status != ElementStatus.DEPRECATED }, sort: 'lastUpdated', order: 'desc', true, ELEMENTS_PER_BATCH, DELAY_AFTER_BATCH
        ) concatWith (
            // we still want to index deprecated data models in case of someone wants to search inside these data models
            // yet the indexing may happen in much slower fashion
            rxService.from(DataModel.where{ status == ElementStatus.DEPRECATED }, sort: 'lastUpdated', order: 'desc', true, ELEMENTS_PER_BATCH, 10 * DELAY_AFTER_BATCH)
        ) flatMap {
            return getDataModelWithDeclaredElements(it)
        } concatWith (
            rxService.from(dataModelService.classified(CatalogueElement, DataModelFilter.create(true)), true, 10 * ELEMENTS_PER_BATCH, DELAY_AFTER_BATCH)
        ) concatWith (
            rxService.from(RelationshipType.where {})
        ) concatWith (
            rxService.from(DataModelPolicy.where {})
        )

        if (soft) {
            return  index(session, elements).doOnError {
                log.error "Exception reindexing catalogue: ${it.getClass()}", it
            }
        }

        return RxElastic.from(client.admin().indices().prepareDelete("${MC_PREFIX}*")).map { it.acknowledged }.concatWith(index(session, elements)).doOnError {
            log.error "Exception reindexing catalogue: ${it.getClass()}", it
        }

    }

    Map<String, Map> getMapping(Class clazz, Boolean toplevel = false) {
        getMapping(clazz, clazz, toplevel)
    }

    Map<String, Map> getMapping(Class clazz, Class implementation, Boolean toplevel = false) {
        CacheService.MAPPINGS_CACHE.get("$clazz=>$implementation[$toplevel]".toString()) {
            getMappingInternal(clazz, implementation, toplevel)
        }
    }

    Map<String, Map> getMappingInternal(Class clazz, Class implementation, Boolean toplevel) {
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

            // the same applies for
            mapping[typeName].properties.data_model = getMapping(DataModel).data_model

            Map catalogueElementMappingCombined = combineMappingForAllCatalogueElements()

            mapping[typeName].properties.source = catalogueElementMappingCombined.catalogue_element
            mapping[typeName].properties.destination = catalogueElementMappingCombined.catalogue_element
        } else if (clazz == CatalogueElement && implementation != DataModel) {
            // source and destination combines all available catalogue element mappings
            // relationship copies relationship type mapping
            mapping[typeName].properties.data_model = getMapping(DataModel).data_model
        }

        if (toplevel) {
            mapping[typeName].date_detection = false
        }

        return mapping

    }


    private Observable<SimpleIndexRequest> toSimpleIndexRequests(IndexingSession session, Observable<Object> entities) {
        entities.groupBy {
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

            if (DataModelPolicy.isAssignableFrom(clazz)) {
                return DataModelPolicy
            }
            return clazz
        } flatMap { group ->
            if (group.key == CatalogueElement) {
                return group.buffer(ELEMENTS_PER_BATCH).flatMap { elements ->
                    from(elements).concatWith(rxService.from(Relationship.where { (source in elements || destination in elements) && relationshipType.searchable == true }, true, ELEMENTS_PER_BATCH, DELAY_AFTER_BATCH))
                }
            }
            if (group.key == Relationship) {
                return group.flatMap { entity -> getRelationshipWithSourceAndDestination(entity as Relationship) }
            }
            if (group.key in [RelationshipType, DataModelPolicy]) {
                return group
            }
            throw new UnsupportedOperationException("Not Yet Implemented for $group.key")
        } flatMap { entity ->
            Class clazz = getEntityClass(entity)
            ImmutableSet<String> indices = getIndices(entity)
            ImmutableSet<Class> mappedClasses = ImmutableSet.of(clazz)
            Document document = session.getDocument(entity)
            ensureIndexExists(session, from(indices), mappedClasses).map {
                new SimpleIndexRequest(indices,  document)
            }
        }
    }

    private Observable<CatalogueElement> getDataModelWithDeclaredElements(DataModel element) {
        just(element as CatalogueElement).concatWith(rxService.from(CatalogueElement.where { dataModel == element }, true, ELEMENTS_PER_BATCH, DELAY_AFTER_BATCH))
    }

    private static Observable<?> getRelationshipWithSourceAndDestination(Relationship rel) {
        just(rel, rel.source, rel.destination)
    }

    private Observable<BulkResponse> bulkIndex(List<SimpleIndexRequest> documents) {
        RxElastic.from { buildBulkIndexRequest(documents) }.flatMap {
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
        }.retryWhen(RxService.withDelay(RxElastic.DEFAULT_RETRIES, RxElastic.DEFAULT_DELAY, [
            EsRejectedExecutionException,
            IndexNotFoundException // sometimes by race condition
        ] as Set))
    }

    private BulkRequestBuilder buildBulkIndexRequest(List<SimpleIndexRequest> indexRequests) {
        BulkRequestBuilder bulkRequest = client.prepareBulk()
        for (SimpleIndexRequest indexRequest in indexRequests) {
            for (String index in indexRequest.indices) {
                bulkRequest.add(client
                    .prepareIndex(index, indexRequest.document.type, indexRequest.document.id)
                    .setVersion(indexRequest.document.version ?: 1L)
                    .setVersionType(VersionType.EXTERNAL_GTE)
                    .setSource(indexRequest.document.payload)
                )
            }
        }
        return bulkRequest
    }


    private Observable<SimpleIndicesExistsResponse> indexExists(IndexingSession session, Observable<String> indices) {
        return indices.flatMap { index ->
            if (session.indexExist(index)) {
                return from(new SimpleIndicesExistsResponse(exists: true, index: index))
            }
            return RxElastic.from(client.admin().indices().prepareExists(index)).map {
                session.indexExist(index, it.exists)
                return new SimpleIndicesExistsResponse(exists: it.exists, index: index)
            }
        }
    }

    private Observable<String> ensureIndexExists(IndexingSession session, Observable<String> indices, Iterable<Class> supportedTypes) {
        return indexExists(session, indices).flatMap { response ->
            if (response.exists) {
                return just(response.index)
            }
            CreateIndexRequestBuilder request = client.admin()
                    .indices()
                    .prepareCreate(response.index)

            for (Class type in supportedTypes) {
                request.addMapping(getTypeName(type), getMapping(type, true))
            }

            RxElastic.from(request).flatMap {
                if (!it.acknowledged) {
                    return ensureIndexExists(session, indices, supportedTypes)
                }
                log.debug "Created index $response.index for following types: $supportedTypes"
                session.indexExist(response.index, true)
                return just(response.index)
            } .onErrorReturn {
                if (it instanceof IndexAlreadyExistsException || it.cause instanceof IndexAlreadyExistsException) {
                    return response.index
                }
                throw it
            }
        }
    }



    protected static ImmutableSet<String> getIndices(object) {
        Class clazz = getEntityClass(object)
        if (DataModel.isAssignableFrom(clazz)) {
            return ImmutableSet.of(getGlobalIndexName(clazz), getDataModelIndex(object as DataModel, clazz))
        }

        if (CatalogueElement.isAssignableFrom(clazz)) {
            CatalogueElement element = object as CatalogueElement
            if (element.dataModel) {
                return ImmutableSet.of(getGlobalIndexName(clazz), getDataModelIndex(element.dataModel, clazz))
            }
            return ImmutableSet.of(getGlobalIndexName(clazz), getOrphanedIndexName(clazz))
        }

        if (RelationshipType.isAssignableFrom(clazz) || DataModelPolicy.isAssignableFrom(clazz)) {
            return ImmutableSet.of(getGlobalIndexName(clazz))
        }

        if (Relationship.isAssignableFrom(clazz)) {
            Relationship rel = object as Relationship
            return ImmutableSet.builder()
                .add(rel.source.dataModel ? getDataModelIndex(rel.source.dataModel, Relationship) : getGlobalIndexName(Relationship))
                .add(rel.destination.dataModel ? getDataModelIndex(rel.destination.dataModel, Relationship) : getGlobalIndexName(Relationship))
                .build()
        }

        throw new UnsupportedOperationException("Not Yet Implemented for $object")
    }

    protected static String getDataModelIndex(Long id, String typeName) {
        "${DATA_MODEL_PREFIX}${id}_${typeName}"
    }

    protected static String getDataModelIndex(DataModel dataModel, Class elementType) {
        getDataModelIndex(dataModel.getId(), getTypeName(elementType))
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
            if (current.containsKey(entry.key) && current[entry.key] instanceof Map) {
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


