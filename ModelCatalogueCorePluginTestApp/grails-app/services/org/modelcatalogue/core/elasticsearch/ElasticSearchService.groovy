package org.modelcatalogue.core.elasticsearch


import com.google.common.collect.ImmutableSet
import grails.util.GrailsNameUtils
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
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
import org.hibernate.ObjectNotFoundException
import org.modelcatalogue.core.*
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.elasticsearch.rx.RxElastic
import org.modelcatalogue.core.rx.RxService
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Lists
import rx.Observable

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.util.concurrent.TimeUnit

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass
import static rx.Observable.from
import static rx.Observable.just

class ElasticSearchService implements SearchCatalogue {

    static transactional = false


    private static final int ELEMENTS_PER_BATCH = readFromEnv('MC_ES_ELEMENTS_PER_BATCH', 30)
    private static final int DELAY_AFTER_BATCH = readFromEnv('MC_ES_DELAY_AFTER_BATCH', 50)

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

    /*
    * Initialise the elasticsearch client
    * local or remote depending on grails application config
    * */

    @PostConstruct
    private void init() {
        if (grailsApplication.config.mc.search.elasticsearch.host || System.getProperty('mc.search.elasticsearch.host')) {
            initRemoteClient()
        } else if (grailsApplication.config.mc.search.elasticsearch.local || System.getProperty('mc.search.elasticsearch.local')) {
            initLocalClient()
        }

    }

    /*
   * create connection to local elasticsearch in temp directory
   * */

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

    /*
     * create remote connection based on config
     * */

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


    //cleanup connection
    @PreDestroy
    public void cleanUp() throws Exception {
        node?.close()
        client?.close()
    }

    /*
    * search catalogue based on catalogue element class, relationship type and relationship direction
    * */

    @Override
    ListWithTotalAndType<Relationship> search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params) {
        if (!type.searchable) {
            return Lists.emptyListWithTotalAndType(Relationship)
        }

        //get any search string from params
        String search = params.search

        //get boolean query builder that matches doc based on boolean combinations of other queries
        //https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()

        if (search != '*') {
            boolQuery.minimumNumberShouldMatch(1)
        }

        if (type) {
            boolQuery.must(QueryBuilders.termQuery('relationship_type.name', type.name))
        }

        List<String> states = []

        //if the role is viewer, don't return elements that they shouldn't see i.e. drafts .
        if (params.status) {
            states = ElementService.getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER'))*.toString()
        }

        List<String> types = []


        //if you only want to search for a particular element class and subclasses of it
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

        //build query based on the relationship direction that you are searching on
        //i.e. are you searching parents of children
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

        //collect indices that need to be searched based on params
        List<String> indicies = collectDataModelIndicies(params, [Relationship])

        //build search request
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

            boolQuery.should(QueryBuilders.prefixQuery('name', search.toLowerCase()).boost(200))
            boolQuery.should(QueryBuilders.nestedQuery('ext', QueryBuilders.termQuery('ext.value', search)).boost(10))

            qb = boolQuery
        } else if (RelationshipType.isAssignableFrom(resource)) {
            indicies = [getGlobalIndexName(resource)]

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumNumberShouldMatch(1)

            CATALOGUE_ELEMENT_BOOSTS.each { String property, int boost ->
                boolQuery.should(QueryBuilders.matchQuery(property, search).boost(boost))
            }

            boolQuery.should(QueryBuilders.prefixQuery('name', search.toLowerCase()).boost(200))

            qb = boolQuery
        } else if (DataModelPolicy.isAssignableFrom(resource)) {
            indicies = [getGlobalIndexName(resource)]

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumNumberShouldMatch(1)

            CATALOGUE_ELEMENT_BOOSTS.each { String property, int boost ->
                boolQuery.should(QueryBuilders.matchQuery(property, search).boost(boost))
            }

            boolQuery.should(QueryBuilders.prefixQuery('name', search.toLowerCase()).boost(200))

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


    // may want to build on this query at a later date
    public <T> ElasticSearchQueryList<T> fuzzySearch(Class<T> resource, Map params) {
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

            boolQuery.should(QueryBuilders.prefixQuery('name', search.toLowerCase()).boost(200))
            boolQuery.should(QueryBuilders.nestedQuery('ext', QueryBuilders.termQuery('ext.value', search)).boost(10))

            qb = boolQuery
        } else if (RelationshipType.isAssignableFrom(resource)) {
            indicies = [getGlobalIndexName(resource)]

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumNumberShouldMatch(1)

            CATALOGUE_ELEMENT_BOOSTS.each { String property, int boost ->
                boolQuery.should(QueryBuilders.matchQuery(property, search).boost(boost))
            }

            boolQuery.should(QueryBuilders.prefixQuery('name', search.toLowerCase()).boost(200))

            qb = boolQuery
        } else if (DataModelPolicy.isAssignableFrom(resource)) {
            indicies = [getGlobalIndexName(resource)]

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumNumberShouldMatch(1)

            CATALOGUE_ELEMENT_BOOSTS.each { String property, int boost ->
                boolQuery.should(QueryBuilders.matchQuery(property, search).boost(boost))
            }

            boolQuery.should(QueryBuilders.prefixQuery('name', search.toLowerCase()).boost(200))

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
//                .setMinScore(0.2)

        return ElasticSearchQueryList.search(params,resource, request)
    }


    //get index name for a class
    protected static String getGlobalIndexName(Class resource) {
        "${GLOBAL_PREFIX}${getTypeName(resource)}"
    }

    //get orphaned name for a class i.e. if a data element doesn't have a model assocaited with it, it is an orphan
    protected static String getOrphanedIndexName(Class resource) {
        "${ORPHANED_INDEX}_${getTypeName(resource)}"
    }

    //get all of the indicies associated with a data model
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

    /*
     * Index for single object rather than set of objects
     * Returns an observable - should return true if the index is successful, returns false if unsuccessful
     * */

    @Override
    Observable<Boolean> index(Object object) {
        // TODO: replace this with a non batch index as this is a single document only
        List toIndex = [object]
        IndexingSession session = IndexingSession.create()
        indexSimpleIndexRequestsInBatches(session, toIndex)
        return just(true)
    }


    /*
    * Index for multiple object rather than set of objects
    * Returns an observable - should return true if the index is successful, returns false if unsuccessful
    * delegates to top
    * */


    @Override
    Observable<Boolean> index(Iterable<Object> resource) {
        // TODO: investigate why the iterable can contain null values
        IndexingSession session = IndexingSession.create()
        indexSimpleIndexRequestsInBatches(session, resource)
        return just(true)
    }


    private Observable<Boolean> indexSimpleIndexRequests(List<SimpleIndexRequest> indexRequests) {
        bulkIndex(indexRequests).flatMap { bulkResponse ->
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
            return unindexInternal(session, object)

        }
        return just(false)
    }

    private Observable<Boolean> unindexInternal(IndexingSession session, Object element) {
        try {
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
        } catch (UnsupportedOperationException e) {
            return Observable.error(e)
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

        deleteIndexes()

        IndexingSession session = IndexingSession.create()

        //index Data Models
        indexSimpleIndexRequestsInBatches(session, DataModel.list())

        //Index Data Classes
        indexSimpleIndexRequestsInBatches(session, DataClass.list())

        //Index Data Element
        indexSimpleIndexRequestsInBatches(session, DataElement.list())

        //Index Data Types
        indexSimpleIndexRequestsInBatches(session, DataType.list())

        //Index Measurement Units
        indexSimpleIndexRequestsInBatches(session, MeasurementUnit.list())

        //Index Tags
        indexSimpleIndexRequestsInBatches(session, Tag.list())

        //Index DataModelPolicy
        indexSimpleIndexRequestsInBatches(session, DataModelPolicy.list())

        //Index Asset
        indexSimpleIndexRequestsInBatches(session, Asset.list())

        //Index RelationshipType
        indexSimpleIndexRequestsInBatches(session, RelationshipType.list())

        //Index Users
        indexSimpleIndexRequestsInBatches(session, User.list())

        //index relationships that are searchable i.e. favourites
        def query = Relationship.where { (relationshipType.searchable == true) }
        indexSimpleIndexRequestsInBatches(session, query.list())


        return Observable.just(true)

    }

    private void deleteIndexes(){
        def indexList = client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().concreteAllIndices()

        for (String index : indexList) {
            if (index.contains("mc")) {
                IndicesExistsResponse res = client.admin().indices().prepareExists(index).execute().actionGet();
                if (res.isExists()) {
                    DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(index)
                    delIdx.execute().actionGet();
                }
            }
        }
    }



    private void indexSimpleIndexRequestsInBatches(IndexingSession session, Iterable<Object> batch) {

        List<Observable> singleRequests = []
        def count = batch.size()

        batch.eachWithIndex { element, i ->
            singleRequests.add(toSimpleIndexRequests(session, element))
            //split indexing into batches
            if ((i + 1) % ELEMENTS_PER_BATCH == 0 || (i + 1) == count) {
                //TODO: can we get rid of placeholder for last
                indexSimpleIndexRequests(singleRequests).toBlocking().last()
                singleRequests.clear()
            }
        }


    }

    private SimpleIndexRequest toSimpleIndexRequests(IndexingSession session, Object element) {
        Class clazz = getEntityClass(element)
        ImmutableSet<String> indices = getIndices(element)
        ImmutableSet<Class> mappedClasses = ImmutableSet.of(clazz)
        Document document = session.getDocument(element)
        ensureIndexExists(session, indices, mappedClasses)
        new SimpleIndexRequest(indices, document)
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
        //Ignore date information NOT mapped as date
        if (toplevel) {
            mapping[typeName].date_detection = false
        }

        return mapping

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


    private void ensureIndexExists(IndexingSession session, ImmutableSet<String> indices, Iterable<Class> supportedTypes) {

        indices.each{ index ->
            SimpleIndicesExistsResponse response

            if (session.indexExist(index)) {
                response = new SimpleIndicesExistsResponse(exists: true, index: index)
            } else{
                IndicesExistsResponse it = client.admin().indices().prepareExists(index).execute().actionGet()
                session.indexExist(index, it.exists)
                response = new SimpleIndicesExistsResponse(exists: it.exists, index: index)
            }


            if (!response.exists) {

            //Create elastic search builder
            CreateIndexRequestBuilder request = client.admin()
                    .indices()
                    .prepareCreate(response.index)

            //add mappings for index
            for (Class type in supportedTypes) {
                request.addMapping(getTypeName(type), getMapping(type, true))
            }

            //create the index
            request.execute().actionGet()

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
            try {
                return ImmutableSet.builder()
                                   .add(rel.source.dataModel ? getDataModelIndex(rel.source.dataModel, Relationship) : getGlobalIndexName(Relationship))
                                   .add(rel.destination.dataModel ? getDataModelIndex(rel.destination.dataModel, Relationship) : getGlobalIndexName(Relationship))
                                   .build()
            } catch (ObjectNotFoundException ignored) {
                return ImmutableSet.of()
            } catch (Error e){
                return ImmutableSet.of()
            }
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
            Long dataModelId
            if(params.get('dataModel') instanceof Long){
                dataModelId = params.get('dataModel')
            }else{
                dataModelId = params.long('dataModel')
            }
            DataModel dataModel = DataModel.get(dataModelId)
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


