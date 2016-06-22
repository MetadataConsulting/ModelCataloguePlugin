package org.modelcatalogue.core.elasticsearch
import groovy.json.JsonOutput
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import spock.util.concurrent.BlockingVariable
import spock.util.concurrent.BlockingVariables

import java.util.concurrent.TimeUnit

class ElasticSearchServiceSpec extends org.modelcatalogue.testapp.AbstractIntegrationSpec {

    ElasticSearchService elasticSearchService
    CatalogueBuilder catalogueBuilder

    def setup() {
        initRelationshipTypes()
        elasticSearchService.initLocalClient()
        elasticSearchService.reindex().toBlocking().subscribe()
    }

    def "play with elasticsearch"() {
        catalogueBuilder.build {
            dataModel(name: "ES Test Model") {
                policy 'TEST POLICY'
                dataClass(name: "Foo") {
                    description "foo bar"
                    ext 'foo', 'bar'
                    ext 'date_in_class_metadata', '2014-06-06T06:34:24Z'
                    relationship {
                       //  ext 'date_in_relationship_metadata', '2014-06-06T06:34:24Z'
                    }
                    validationRule(name: 'Test Rule') {
                        rule 'IF this THEN that'
                    }
                    dataElement(name: 'nested data element') {
                        dataType(name: 'primitive type') {
                            measurementUnit(name: 'unit of measure 123456', symbol: 'uom')
                        }
                    }
                }
            }
            dataModelPolicy(name: 'TEST POLICY') {
                check dataType property 'name' is 'unique'
            }
        }
        DataModel dataModel = DataModel.findByName("ES Test Model")
        DataClass element = DataClass.findByName("Foo")
        MeasurementUnit unit = MeasurementUnit.findByName('unit of measure 123456')
        DataModelPolicy policy = DataModelPolicy.findByName('TEST POLICY')

        Class elementClass = HibernateHelper.getEntityClass(element)

        String index = elasticSearchService.getDataModelIndex(dataModel)
        String type  = elasticSearchService.getTypeName(elementClass)

        expect:
        dataModel
        element
        unit
        policy


        when:
        BlockingVariables results = new BlockingVariables(60)

        elasticSearchService.unindex(dataModel).subscribe {
            results.dataModelUnindexed = true
            elasticSearchService.unindex(element).subscribe {
                results.elementUnindexed = true
                elasticSearchService.index(dataModel).subscribe {
                    results.dataModelIndexed = true
                    elasticSearchService.index(element).subscribe {
                        results.elementIndexed = true
                    }
                    elasticSearchService.index(unit).subscribe {
                        results.unitIndexed = true
                    }
                }
            }
        }

        then:
        noExceptionThrown()
        results.dataModelUnindexed
        results.elementUnindexed
        results.dataModelIndexed
        results.elementIndexed
        results.unitIndexed

        elasticSearchService.client
                .prepareGet(index, type, element.getId().toString())
                .execute()
                .get().exists

        when:
        boolean found = false
        ListWithTotalAndType<DataClass> foundClasses = Lists.emptyListWithTotalAndType(DataClass)
        for (int i = 0; i < 100; i++) {
            foundClasses = elasticSearchService.search(DataClass, [search: 'foo'])
            found = foundClasses.total == 1L
            if (found) {
                break
            }
            Thread.sleep(100)
        }

        then:
        found
        element in foundClasses.items


        when: "search with the content of item name in relationships"
        ListWithTotalAndType<Relationship> foundRelationships = elasticSearchService.search(dataModel, RelationshipType.hierarchyType, RelationshipDirection.OUTGOING,[search: 'test'])

        then: "there are no results if the related item does not contain the search term"
        foundRelationships.total == 0L

        when:
        ListWithTotalAndType<DataModel> foundDataModels = elasticSearchService.search(DataModel, [search: 'test'])

        then:
        foundDataModels.total == 1L
        dataModel in foundDataModels.items

        when:
        BlockingVariable<Boolean> policyIndexed = new BlockingVariable<Boolean>(60)
        elasticSearchService.index(policy).subscribe {
            policyIndexed.set(true)
        }

        then:
        policyIndexed.get()
        retry (10, 100) { elasticSearchService.search(DataModelPolicy, [search: 'test policy', max: '1']).total == 1L }
    }

    def "index user"() {
        BlockingVariable<Boolean> userIndexed = new BlockingVariable<>(60, TimeUnit.SECONDS)

        when:
        elasticSearchService.index(new User(name: 'tester', username: 'tester', password: 'tester').save(failOnError: true)).subscribe {
            userIndexed.set(true)
        }

        then:
        userIndexed.get()
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
        mapping.relationship.properties.source.properties.data_class
        mapping.relationship.properties.source.properties.measurement_unit
        mapping.relationship.properties.source.properties.data_type
        mapping.relationship.properties.source.properties.data_type.properties
        mapping.relationship.properties.source.properties.data_type.properties.measurement_unit
        mapping.relationship.properties.source.properties.data_type.properties.measurement_unit.properties
        mapping.relationship.properties.source.properties.data_type.properties.measurement_unit.properties.data_model
        mapping.relationship.properties.destination
    }


    def "test import MET-523"() {
        when:
        catalogueBuilder.build {
            dataModel name: 'MET-523', {
                dataClass name: 'MET-523.M1', {
                    dataElement name: 'MET-523.M1.DE1', {
                        dataType name: 'MET-523.M1.VD1', {
                            measurementUnit name: 'MET-523.MU1'
                        }
                    }
                    dataElement name: 'MET-523.M1.DE2', {
                        dataType name: 'MET-523.M1.VD2', {
                            measurementUnit name: 'MET-523.MU2'
                        }
                    }
                    dataElement name: 'MET-523.M1.DE3', {
                        dataType name: 'MET-523.M1.VD3', {
                            measurementUnit name: 'MET-523.MU3'
                        }
                    }
                    dataElement name: 'MET-523.M1.DE4', {
                        dataType name: 'MET-523.M1.VD4', {
                            measurementUnit name: 'MET-523.MU4'
                        }
                    }
                    dataElement name: 'MET-523.M1.DE5', {
                        dataType name: 'MET-523.M1.VD5', {
                            measurementUnit name: 'MET-523.MU5'
                        }
                    }
                }
                dataClass name: 'MET-523.M2', {
                    dataElement name: 'MET-523.M2.DE1', {
                        dataType name: 'MET-523.M2.VD1', {
                            measurementUnit name: 'MET-523.MU1'
                        }
                    }
                    dataElement name: 'MET-523.M2.DE2', {
                        dataType name: 'MET-523.M2.VD2', {
                            measurementUnit name: 'MET-523.MU2'
                        }
                    }
                    dataElement name: 'MET-523.M2.DE3', {
                        dataType name: 'MET-523.M2.VD3', {
                            measurementUnit name: 'MET-523.MU3'
                        }
                    }
                    dataElement name: 'MET-523.M2.DE4', {
                        dataType name: 'MET-523.M2.VD4', {
                            measurementUnit name: 'MET-523.MU4'
                        }
                    }
                    dataElement name: 'MET-523.M2.DE5', {
                        dataType name: 'MET-523.M2.VD5', {
                            measurementUnit name: 'MET-523.MU5'
                        }
                    }
                }
                dataClass name: 'MET-523.M3', {
                    dataElement name: 'MET-523.M3.DE1', {
                        dataType name: 'MET-523.M3.VD1', {
                            measurementUnit name: 'MET-523.MU1'
                        }
                    }
                    dataElement name: 'MET-523.M3.DE2', {
                        dataType name: 'MET-523.M3.VD2', {
                            measurementUnit name: 'MET-523.MU2'
                        }
                    }
                    dataElement name: 'MET-523.M3.DE3', {
                        dataType name: 'MET-523.M3.VD3', {
                            measurementUnit name: 'MET-523.MU3'
                        }
                    }
                    dataElement name: 'MET-523.M3.DE4', {
                        dataType name: 'MET-523.M3.VD4', {
                            measurementUnit name: 'MET-523.MU4'
                        }
                    }
                    dataElement name: 'MET-523.M3.DE5', {
                        dataType name: 'MET-523.M3.VD5', {
                            measurementUnit name: 'MET-523.MU5'
                        }
                    }
                }
            }
        }

        DataModel model = DataModel.findByName('MET-523')

        then:
        CatalogueElement.where {
            dataModel == model
        }.count() == 39

        when:
        elasticSearchService.reindex().toBlocking().last()

        then:
        noExceptionThrown()
    }

    private static boolean retry(int times, long timeout, Closure<Boolean> test) {
        for (int i = 0; i < times; i++) {
            if (test()) {
                return true
            }
            Thread.sleep(timeout)
        }
        return false
    }

}
