package org.modelcatalogue.core.elasticsearch

import grails.test.spock.IntegrationSpec
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.hibernate.proxy.HibernateProxyHelper
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.RelationshipDirection
import rx.Observable


class ElasticSearchServiceSpec extends IntegrationSpec {

    ElasticSearchService elasticSearchService
    InitCatalogueService initCatalogueService
    CatalogueBuilder catalogueBuilder
    Node node

    @Rule TemporaryFolder data

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        File dataFolder = data.newFolder('elasticsearch')

        // keep this for unit tests
         node = NodeBuilder.nodeBuilder().settings(Settings.builder().put('path.home', dataFolder.canonicalPath).build()).local(true).node()
         Client client = node.client()

        // testing with docker instance
//        Settings settings = Settings.settingsBuilder()
//                .put("client.transport.sniff", false)
//                .put("client.transport.ignore_cluster_name", false).build();
//        Client client = TransportClient
//                .builder()
//                .settings(settings)
//                .build()
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.99.100"), 9300))

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
                .concatWith(elasticSearchService.unindex(element))
                .concatWith(elasticSearchService.index(dataModel))
                .concatWith(elasticSearchService.index(element))
                .toBlocking().last()

        Thread.sleep(1000) // some time to index
        then:
        noExceptionThrown()
        elasticSearchService.client
                .prepareGet(index, type, element.getId().toString())
                .execute()
                .get().exists

        when:


        ListWithTotalAndType<DataClass> foundClasses = elasticSearchService.search(DataClass, [search: 'foo'])

        then:
        foundClasses.total == 1L
        element in foundClasses.items


        when: "search with the content of item name in relationships"
        ListWithTotalAndType<Relationship> foundRelationships = elasticSearchService.search(dataModel, RelationshipType.hierarchyType, RelationshipDirection.BOTH,[search: 'test'])

        then: "there are no results if the related item does not contain the search term"
        foundRelationships.total == 0L

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
