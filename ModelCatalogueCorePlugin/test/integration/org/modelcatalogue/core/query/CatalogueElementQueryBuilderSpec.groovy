package org.modelcatalogue.core.query

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.RelationshipType

class CatalogueElementQueryBuilderSpec extends IntegrationSpec {

    def initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    def "Can query by name"() {
        new DataType(name: 'DT4QBBN').save(failOnError: true)

        CatalogueElementQueryBuilder<DataType> query = CatalogueElementQueryBuilder.create(DataType) {
            property {
                eq 'name', 'DT4QBBN'
            }
        }

        expect:
        query.criteria.count() == 1
    }


    def "Can query by metadata"() {
        DataType type = new DataType(name: 'DT4QBBM').save(failOnError: true)
        type.ext.foo = 'bar'

        CatalogueElementQueryBuilder<DataType> query = CatalogueElementQueryBuilder.create(DataType) {
            metadata {
                eq 'extensionValue', 'bar'
            }
        }

        expect:
        query.criteria.count() == 1
    }

    def "Can query by relationship"() {
        DataType type = new DataType(name: 'DT4QBBR1').save(failOnError: true)
        DataType base = new DataType(name: 'DT4QBBR2').save(failOnError: true)

        type.addToIsBasedOn base

        CatalogueElementQueryBuilder<DataType> query1 = CatalogueElementQueryBuilder.create(DataType) {
            incoming(RelationshipType.baseType) {
                eq 'name', 'DT4QBBR2'
            }
        }

        expect:
        query1.criteria.count() == 1

        when:
        CatalogueElementQueryBuilder<DataType> query2 = CatalogueElementQueryBuilder.create(DataType) {
            outgoing(RelationshipType.baseType) {
                eq 'name', 'DT4QBBR1'
            }
        }

        then:
        query2.criteria.count() == 1
    }


}
