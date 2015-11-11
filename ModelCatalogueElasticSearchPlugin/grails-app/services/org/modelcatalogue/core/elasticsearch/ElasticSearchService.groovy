package org.modelcatalogue.core.elasticsearch

import grails.util.GrailsNameUtils
import groovy.json.JsonSlurper
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.node.Node
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.SearchCatalogue
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.RelationshipDirection

// FIXME: do async/batch where possible
class ElasticSearchService implements SearchCatalogue {

    private static String DATA_MODEL_INDEX = "global_data_model"
    private static String ORPHANED_INDEX = "global_catalogue_element"

    private Map<Class, DocumentSerializer> documentSerializers = [
        (CatalogueElement) : new CatalogueElementDocumentSerializer()
    ]

    private Set<Class> typesSupportedInDataModel = [
            Asset, DataClass, DataElement, DataType, EnumeratedType, MeasurementUnit, PrimitiveType, ReferenceType
    ]


    Client client

    @Override
    ListWithTotalAndType<Relationship> search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params) {
        throw new UnsupportedOperationException("Not Yet Implemented")
    }

    @Override
    def <T> ListWithTotalAndType<T> search(Class<T> resource, Map params) {
        throw new UnsupportedOperationException("Not Yet Implemented")
    }

    @Override
    ListWithTotalAndType<CatalogueElement> search(Map params) {
        throw new UnsupportedOperationException("Not Yet Implemented")
    }

    @Override
    void index(Object object) {
        Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(object)
        if (DataModel.isAssignableFrom(clazz)) {
            if (!client.admin().indices().prepareExists(DATA_MODEL_INDEX).execute().get().exists) {
                // data model index only supports data models
                client.admin()
                        .indices()
                        .prepareCreate(DATA_MODEL_INDEX)
                        .addMapping(getTypeName(DataModel), getMapping(DataModel))
                        .execute()
                        .get()
            }
            return
        }
        if (CatalogueElement.isAssignableFrom(clazz)) {
            CatalogueElement element = object as CatalogueElement
            for (String index in (element.dataModels ? element.dataModels.collect { getIndexNameFor(it) } : [ORPHANED_INDEX])) {
                    // FIXME possible bottleneck
                    if (!client.admin().indices().prepareExists(index).execute().get().exists) {
                        // data model index only supports data models
                        CreateIndexRequestBuilder request = client.admin()
                                .indices()
                                .prepareCreate(index)

                        for (Class type in typesSupportedInDataModel) {
                            request.addMapping(getTypeName(type), getMapping(type))
                        }
                        request.execute().get()
                    }
                    client.prepareIndex(index, getTypeName(clazz), element.getId().toString())
                            .setSource(getDocument(element))
                            .execute()
                            .get()
            }
            return

        }
        throw new UnsupportedOperationException("Not Yet Implemented for $object")
    }

    @Override
    void index(Iterable<Object> resource) {
        for (o in resource) {
            index(o)
        }
    }

    @Override
    void unindex(Object object) {
        Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(object)
        if (DataModel.isAssignableFrom(clazz)) {
            String indexName = getIndexNameFor(object as DataModel)
            if (client.admin().indices().prepareExists(indexName).execute().get().exists) {
                client.admin().indices().prepareDelete(indexName).execute().get()
            }
            return
        }
        if (CatalogueElement.isAssignableFrom(clazz)) {
            CatalogueElement element = object as CatalogueElement
            for (String index in (element.dataModels ? element.dataModels.collect { getIndexNameFor(it) } : [ORPHANED_INDEX])) {
                if (client.prepareGet(index, getTypeName(clazz), "${element.getId()}").setFields(new String[0]).execute().get().exists) {
                    client.prepareDelete(index, getTypeName(clazz), "${element.getId()}").execute().get()
                }
            }
            return
        }
        throw new UnsupportedOperationException("Not Yet Implemented for $object")
    }

    String getIndexNameFor(DataModel dataModel) {
        "data_model_${dataModel.getId()}"
    }

    @Override
    void unindex(Collection<Object> object) {
        for (o in object) {
            unindex(o)
        }
    }

    @Override
    void refresh() {

    }

    public Map<String, Object> getDocument(Object object) {
        if (!object) {
            return [:]
        }
        getSerializer(object.class).getDocument(object)
    }

    public <T> DocumentSerializer<? super T> getSerializer(Class<T> clazz) {
        DocumentSerializer<T> serializer = documentSerializers[clazz]

        Class current = clazz
        while (serializer == null) {
            current = current.superclass
            serializer = documentSerializers[current]

            if (current == Object && !serializer) {
                throw new IllegalArgumentException("Cannot find serializer for $clazz")
            }
        }

        return serializer
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

        return mapping
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
}
