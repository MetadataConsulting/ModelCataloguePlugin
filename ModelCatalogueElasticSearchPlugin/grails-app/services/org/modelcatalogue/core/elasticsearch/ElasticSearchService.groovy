package org.modelcatalogue.core.elasticsearch

import grails.util.GrailsNameUtils
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Auditor
import org.modelcatalogue.core.audit.CompoundAuditor
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.RelationshipDirection
import rx.Observable

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

import static rx.Observable.from
import static rx.Observable.just

// FIXME: do async/batch where possible - this is partially done - index is now running in batches but can be improved
// FIXME: there should be global index as well to prevent duplicate entries returned from global search
// TODO: JSON from the search service should only return partial response (id, name...) to skip database round trip
class ElasticSearchService implements SearchCatalogue {

    private static String GLOBAL_PREFIX = "global_"
    private static String DATA_MODEL_INDEX = "${GLOBAL_PREFIX}data_model"
    private static String ORPHANED_INDEX = "{GLOBAL_PREFIX}catalogue_element"
    private static Map<String, Integer> CATALOGUE_ELEMENT_BOOSTS = [

            name: 100,
            full_version: 90,
            latest_id: 80,
            // id : 70,
            // latest_version: 70,
            'ext.extension_value.value' : 10,
            description: 1
    ]

    private Set<Class> typesSupportedInDataModel = [
            Asset, DataClass, DataElement, DataType, EnumeratedType, MeasurementUnit, PrimitiveType, ReferenceType, Relationship
    ]

    GrailsApplication grailsApplication
    DataModelService dataModelService
    AuditService auditService
    Node node
    Client client

    @PostConstruct
    private void init() {
        if (grailsApplication.config.mc.search.elasticsearch.local) {
            node = NodeBuilder.nodeBuilder()
                    .settings(Settings.builder().put('path.home', grailsApplication.config.mc.search.elasticsearch.local.toString()).build())
                    .local(true).node()

            client = node.client()

            Closure<Auditor> oldFactory = auditService.auditorFactory
            auditService.auditorFactory = { CompoundAuditor.from(oldFactory(), new ElasticSearchServiceNotifier(this))}

            log.info "Using local ElasticSearch instance in directory ${grailsApplication.config.mc.search.elasticsearch.local}"
        } else if (grailsApplication.config.mc.search.elasticsearch.host || System.getProperty('mc.search.elasticsearch.host')) {
            String host = grailsApplication.config.mc.search.elasticsearch.host ?: System.getProperty('mc.search.elasticsearch.host')
            String port = grailsApplication.config.mc.search.elasticsearch.port ?: System.getProperty('mc.search.elasticsearch.port') ?: "9300"
            Settings.Builder settingBuilder = Settings.builder()

            if (grailsApplication.config.mc.search.elasticsearch.settings) {
                grailsApplication.config.mc.search.elasticsearch.settings(settingBuilder)
            }

            client = TransportClient
                    .builder()
                    .settings(settingBuilder)
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), Integer.parseInt(port, 10)))

            Closure<Auditor> oldFactory = auditService.auditorFactory
            auditService.auditorFactory = { CompoundAuditor.from(oldFactory(), new ElasticSearchServiceNotifier(this))}

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
        // TODO: implement
        throw new UnsupportedOperationException("Not Yet Implemented")
    }

    @Override
    def <T> ListWithTotalAndType<T> search(Class<T> resource, Map params) {
        String search = params.search
        QueryBuilder qb
        List<String> indicies

        if (CatalogueElement.isAssignableFrom(resource)) {
            indicies = collectDataModelIndicies(params)

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()

            if (params.status) {
                boolQuery.must(QueryBuilders.termQuery('status', params.status.toString().toUpperCase()))
            }

            CATALOGUE_ELEMENT_BOOSTS.each { String property, int boost ->
                boolQuery.should(QueryBuilders.matchQuery(property, search).boost(boost))
            }

            boolQuery.should(QueryBuilders.prefixQuery('name', search).boost(200))

            qb = boolQuery
        } else if (RelationshipType.isAssignableFrom(resource)) {
            indicies = [getGlobalIndexName(resource)]

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()

            if (params.status) {
                boolQuery.must(QueryBuilders.termQuery('status', params.status.toString().toUpperCase()))
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

        // log.debug "searching $qb"

        SearchRequestBuilder request = client
                .prepareSearch(indicies as String[])
                .setTypes(collectTypes(resource) as String[])
                .addField('_id')
                .setQuery(qb)


        return ElasticSearchQueryList.search(params,resource, request)
    }

    private static String getGlobalIndexName(Class resource) {
        "${GLOBAL_PREFIX}${getTypeName(resource)}"
    }

    List<String> collectTypes(Class<?> resource) {
        collectSubclasses(resource).collect { getTypeName(it) }
    }

    List<Class> collectSubclasses(Class<?> resource) {
        // TODO: this should be cached
        GrailsDomainClass domainClass = grailsApplication.getDomainClass(resource.name) as GrailsDomainClass

        if (domainClass.hasSubClasses()) {
            return [resource] + domainClass.subClasses.collect { it.clazz }
        }
        return [resource]
    }

    private List<String> collectDataModelIndicies(Map params) {
        DataModelFilter filter = getOverridableDataModelFilter(params)

        if (!filter) {
            return [everyDataModelIndex] // search all by default
        }

        if (filter.unclassifiedOnly) {
            return [ORPHANED_INDEX]
        }

        if (filter.includes) {
            // excludes are ignored if there are includes
            return filter.includes.collect { getDataModelIndex(it) }
        }

        if (filter.excludes) {
            return [everyDataModelIndex] + filter.excludes.collect { "-${getDataModelIndex(it)}" }
        }

        throw new IllegalStateException("Unknown filter setup: $filter")
    }

    @Override
    ListWithTotalAndType<CatalogueElement> search(Map params) {
        search CatalogueElement, params
    }

    @Override
    void index(Object object) {
        log.debug "Indexing $object"

        Observable<SimpleIndexResponse> indexing = indexAsync(object)

        indexing.doOnError { throwable ->
            log.error("Exception while indexing", throwable)
        }

        indexing.doOnNext { indexResponse ->
            log.info("got index response: $indexResponse")
        }

        // TODO: should be returning observable and not be blocking
        indexing.toBlocking().last()
    }

    private Observable<SimpleIndexResponse> indexAsync(object) {
        Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(object)
        if (DataModel.isAssignableFrom(clazz)) {
            return safeIndex(just(DATA_MODEL_INDEX).cache(), getElementWithRelationships(object as CatalogueElement) , [DataModel])
        }

        if (CatalogueElement.isAssignableFrom(clazz)) {
            CatalogueElement element = object as CatalogueElement
            return safeIndex(getIndices(element), getElementWithRelationships(element), typesSupportedInDataModel)
        }

        if (RelationshipType.isAssignableFrom(clazz)) {
            return safeIndex(just(getGlobalIndexName(clazz)).cache(), just(object).cache(), [clazz])
        }

        if (Relationship.isAssignableFrom(clazz)) {
            Relationship rel = object as Relationship
            return safeIndex(getIndices(rel.source).mergeWith(getIndices(rel.destination)).cache(), just(object).cache(), typesSupportedInDataModel)
        }

        throw new UnsupportedOperationException("Not Yet Implemented for $object")
    }

    private static Observable<Object> getElementWithRelationships(CatalogueElement element) {
        return just(element as Object)
                .mergeWith(from(element.incomingRelationships))
                .mergeWith(from(element.outgoingRelationships))
                .cache()
    }

    private Observable<SimpleIndexResponse> safeIndex(Observable<String> indicies, Observable<Object> objects, Iterable<Class> supportedTypes) {
        return indexInternal(ensureIndexExists(indicies, supportedTypes), objects)
    }


    private Observable<SimpleIndexResponse> indexInternal(Observable<String> index, Observable<Object> objects) {
        return index.flatMap { idx ->
            BulkRequestBuilder bulkRequest = client.prepareBulk()

            // TODO: use take(time) to split by time
            // wait until finished
            for (Object object in objects.toBlocking().toIterable()) {
                bulkRequest.add(client
                        .prepareIndex(idx, getTypeName(HibernateProxyHelper.getClassWithoutInitializingProxy(object)), object.getId().toString())
                        .setSource(getDocument(object))
                )
            }

            return from(bulkRequest.execute()).flatMap { bulkResponse ->
                from(bulkResponse.items).map { bulkResponseItem ->
                    SimpleIndexResponse.from(bulkResponseItem)
                }
            }
        }
    }

    private Observable<SimpleIndicesExistsResponse> indexExists(Observable<String> indices) {
        return indices.flatMap { index ->
            return from(client.admin().indices().prepareExists(index).execute()).map {
                return new SimpleIndicesExistsResponse(exists: it, index: index)
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
            return from(request.execute()).map {
                return response.index
            }
        }
    }


    private static Observable<String> getIndices(CatalogueElement element) {
        if (element.dataModels) {
            return from(element.dataModels).map {
                getDataModelIndex(it)
            }.cache()
        }
        return just(ORPHANED_INDEX).cache()
    }

    @Override
    void index(Iterable<Object> resource) {
        for (o in resource) {
            index(o)
        }
    }

    @Override
    void unindex(Object object) {
        log.debug "Unindexing $object"
        Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(object)
        if (DataModel.isAssignableFrom(clazz)) {
            String indexName = getDataModelIndex(object as DataModel)
            if (client.admin().indices().prepareExists(indexName).execute().actionGet().exists) {
                client.admin().indices().prepareDelete(indexName).execute().actionGet()
            }
            return
        }
        if (CatalogueElement.isAssignableFrom(clazz)) {
            CatalogueElement element = object as CatalogueElement
            for (String index in (element.dataModels ? element.dataModels.collect {
                getDataModelIndex(it)
            } : [ORPHANED_INDEX])) {
                try {
                    // if (client.prepareGet(index, getTypeName(clazz), "${element.getId()}").setFields(new String[0]).execute().actionGet().exists) {
                    // ignore if does not exist yet
                    client.prepareDelete(index, getTypeName(clazz), "${element.getId()}").execute().actionGet()
                    // }
                } catch (Exception e) {
                    // index probably does not exist yet
                    log.debug "Exception unindexing $element", e
                }

            }
            return
        }
        throw new UnsupportedOperationException("Not Yet Implemented for $object")
    }

    static String getDataModelIndex(Long id) {
        "data_model_${id}"
    }

    static String getDataModelIndex(DataModel dataModel) {
        getDataModelIndex(dataModel.getId())
    }

    static String getEveryDataModelIndex() {
        "data_model_*"
    }

    @Override
    void unindex(Collection<Object> object) {
        for (o in object) {
            unindex(o)
        }
    }

    @Override
    void reindex() {
        int total = DataModel.count() + 2
        DataModel.list().eachWithIndex { DataModel model, i ->
            log.info "[${i + 1}/$total] Reindexing data model ${model.name} (${model.combinedVersion}) - ${model.countOutgoingRelationshipsByType(RelationshipType.declarationType)} items"
            unindex(model)
            index(model)
            for (CatalogueElement element in model.getOutgoingRelationsByType(RelationshipType.declarationType)) {
                unindex(element)
                index(element)
            }
        }

        log.info "[${total - 1}/$total] Reindexing orphaned elements"
        dataModelService.classified(CatalogueElement, DataModelFilter.create(true)).list().each { CatalogueElement element ->
            unindex(element)
            index(element)
        }

        log.info "[${total}/$total] Reindexing relationship types"
        RelationshipType.list().each {
            unindex(it)
            index(it)
        }

    }

    public Map<String, Object> getDocument(Object object) {
        if (!object) {
            return [:]
        }
        DocumentSerializer.Registry.get(object.class).getDocument(object)
    }

    Map<String, Map> getMapping(Class clazz) {
        if (!clazz) {
            return [:]
        }

        Map<String, Map> mapping = [(getTypeName(clazz)): [:]]

        if (clazz.superclass && clazz.superclass != Object) {
            merge mapping[getTypeName(clazz)], getMapping(clazz.superclass)[getTypeName(clazz.superclass)]
        }



        URL url = ElasticSearchService.getResource("${clazz.simpleName}.mapping.json")


        if (url) {
            File mappingFile = new File(url.toURI())
            Map parsed = new JsonSlurper().parse(mappingFile) as Map
            Map parsedMapping = parsed[getTypeName(clazz)] as Map
            if (!parsedMapping) {
                log.warn "the mapping does not contain expected root entry ${getTypeName(clazz)}"
            } else {
                merge mapping[getTypeName(clazz)], parsedMapping
            }
        }

        if (clazz == Relationship) {
            // source and destination combines all available catalogue element mappings
            // relationship copies relationship type mapping
            mapping.relationship_type = getMapping(RelationshipType)

            Map catalogueElementMappingComibied = combineMappingForAllCatalogueElements()

            mapping.source = catalogueElementMappingComibied
            mapping.destination = catalogueElementMappingComibied
        }

        return mapping
    }

    private Map combineMappingForAllCatalogueElements() {
        collectSubclasses(CatalogueElement).inject([:] as Map) { Map collector, Class type -> merge collector, getMapping(type) } as Map
    }


    static String getTypeName(Class clazz) {
        GrailsNameUtils.getNaturalName(clazz.simpleName).replaceAll(/\s/, '_').toLowerCase()
    }

    private static void merge(Map<String, Map> current, Map<String, Map> fromSuper) {
        for (Map.Entry<String, Map> entry in fromSuper) {
            if (current.containsKey(entry.key)) {
                current[entry.key].putAll entry.value
            } else {
                current[entry.key] = entry.value
            }
        }
    }

    private DataModelFilter getOverridableDataModelFilter(Map params) {
        if (params.dataModel) {
            DataModel dataModel = DataModel.get(params.long('dataModel'))
            if (dataModel) {
                return DataModelFilter.includes(dataModel)
            }
        }
        dataModelService.dataModelFilter
    }
}

class SimpleIndicesExistsResponse {
    boolean exists
    String index
}

class SimpleIndexResponse {
    final String index
    final String type
    final String id
    final boolean ok

    SimpleIndexResponse(String index, String type, String id, boolean ok) {
        this.index = index
        this.type = type
        this.id = id
        this.ok = ok
    }

    static SimpleIndexResponse from(BulkItemResponse bulkItemResponse) {
        return new SimpleIndexResponse(
                bulkItemResponse.index,
                bulkItemResponse.type,
                bulkItemResponse.id,
                !bulkItemResponse.failed
        )
    }
}
