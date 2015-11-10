package org.modelcatalogue.core.elasticsearch

import grails.test.spock.IntegrationSpec
import org.elasticsearch.client.Client
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.modelcatalogue.core.CatalogueElement
import spock.lang.Ignore

@Ignore
class ElasticSearchServiceSpec extends IntegrationSpec {

    Node node
    Client client

    ElasticSearchService elasticSearchService

    def setup() {
        node = NodeBuilder.nodeBuilder().settings {
            http.enabled = false
        }.build()
        client = node.client()
    }

    def cleanup() {
        node?.close()
        client?.close()
    }

    def "play with elasticsearch"() {
        when:
        true
        then:
        noExceptionThrown()
    }


    def "read mapping"() {
        expect:
        elasticSearchService.getMapping(CatalogueElement)
    }

}
