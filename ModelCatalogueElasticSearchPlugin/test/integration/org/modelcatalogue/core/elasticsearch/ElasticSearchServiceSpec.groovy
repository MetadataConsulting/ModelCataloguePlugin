package org.modelcatalogue.core.elasticsearch
import grails.test.spock.IntegrationSpec
import groovy.json.JsonOutput
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWithTotalAndType

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
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.9"), 9300))

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
                    ext 'foo', 'bar'
                    ext 'date_in_class_metadata', '2014-06-06T06:34:24Z'
                    relationship {
                       //  ext 'date_in_relationship_metadata', '2014-06-06T06:34:24Z'
                    }
                }
            }
        }
        DataModel dataModel = DataModel.findByName("ES Test Model")
        DataClass element = DataClass.findByName("Foo")

        Class elementClass = HibernateHelper.getEntityClass(element)

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

        when:
        ListWithTotalAndType<DataModel> foundDataModels = elasticSearchService.search(DataModel, [search: 'test'])

        then:
        foundDataModels.total == 1L
        dataModel in foundDataModels.items

    }


    def "read catalogue element mapping"() {
        def mapping = elasticSearchService.getMapping(CatalogueElement)
        println JsonOutput.prettyPrint(JsonOutput.toJson(mapping))
        expect:
        mapping
        mapping.catalogue_element
        mapping.catalogue_element.properties
        mapping.catalogue_element.properties.name
    }

    def "read data element mapping"() {
        def mapping = elasticSearchService.getMapping(DataElement)
        println JsonOutput.prettyPrint(JsonOutput.toJson(mapping))
        expect:
        mapping
        mapping.data_element
        mapping.data_element.properties
        mapping.data_element.properties.name
        mapping.data_element.properties.data_type
    }

    def "read relationship mapping"() {
        def mapping = elasticSearchService.getMapping(Relationship)
        println JsonOutput.prettyPrint(JsonOutput.toJson(mapping))
        expect:
        mapping
        mapping.relationship
        mapping.relationship.properties
        mapping.relationship.properties.ext
        mapping.relationship.properties.relationship_type
        mapping.relationship.properties.source
        mapping.relationship.properties.source.properties
        mapping.relationship.properties.source.properties.data_type
        mapping.relationship.properties.source.properties.data_class
        mapping.relationship.properties.source.properties.measurement_unit
        mapping.relationship.properties.destination
    }

}
