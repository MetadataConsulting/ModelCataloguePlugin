package org.modelcatalogue.core.elasticsearch

import grails.test.spock.IntegrationSpec
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.Node
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.ListWithTotalAndType


class ElasticSearchServiceSpec extends IntegrationSpec {

    ElasticSearchService elasticSearchService
    InitCatalogueService initCatalogueService
    CatalogueBuilder catalogueBuilder
    Node node

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        // keep this for unit tests
        // node = NodeBuilder.nodeBuilder().settings(Settings.builder().put('path.home', '/tmp').build()).local(true).node()
        // Client client = node.client()

        // testing with docker instance
        Settings settings = Settings.settingsBuilder()
                .put("client.transport.sniff", false)
                .put("client.transport.ignore_cluster_name", false).build();
        Client client = TransportClient
                .builder()
                .settings(settings)
                .build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.99.100"), 9300))

        elasticSearchService.client = client

    }

    def cleanup() {
        node?.close()
        elasticSearchService.client?.close()

        node = null
        elasticSearchService.client = null
    }

    def "play with elasticsearch"() {
        catalogueBuilder.build {
            dataModel(name: "ES Test Model") {
                dataClass(name: "Foo") {
                    description "foo bar"
                }
            }
        }
        DataModel dataModel = DataModel.findByName("ES Test Model")
        DataClass element = DataClass.findByName("Foo")

        Class elementClass = HibernateProxyHelper.getClassWithoutInitializingProxy(element)

        String index = elasticSearchService.getDataModelIndex(dataModel)
        String type  = elasticSearchService.getTypeName(elementClass)

        expect:
        dataModel
        element


        when:
        // deletes index "data_model_<id>"
        elasticSearchService.unindex(dataModel)
        elasticSearchService.index(element)

        then:
        noExceptionThrown()
        elasticSearchService.client
                .prepareGet(index, type, element.getId().toString())
                .execute()
                .get().exists

        when:
        Thread.sleep(1000) // some time to index


        ListWithTotalAndType<DataClass> found = elasticSearchService.search(DataClass, [search: 'foo'])

        then:
        found.total == 1L
        element in found.items
    }


    def "read catalogue element mapping"() {
        def mapping = elasticSearchService.getMapping(CatalogueElement)
        println mapping
        expect:
        mapping
        mapping.catalogue_element
        mapping.catalogue_element.properties
        mapping.catalogue_element.properties.name
    }

    def "read data element mapping"() {
        def mapping = elasticSearchService.getMapping(DataElement)
        println mapping
        expect:
        mapping
        mapping.data_element
        mapping.data_element.properties
        mapping.data_element.properties.name
        mapping.data_element.properties.data_type
    }

}
