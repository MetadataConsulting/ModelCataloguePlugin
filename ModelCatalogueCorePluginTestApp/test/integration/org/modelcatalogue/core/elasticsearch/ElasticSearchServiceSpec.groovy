package org.modelcatalogue.core.elasticsearch
import groovy.json.JsonOutput
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import spock.lang.IgnoreIf
import spock.util.concurrent.BlockingVariable
import spock.util.concurrent.BlockingVariables

import java.util.concurrent.TimeUnit

class ElasticSearchServiceSpec extends AbstractIntegrationSpec {

    ElasticSearchService elasticSearchService
    CatalogueBuilder catalogueBuilder

    def setup() {
        initRelationshipTypes()
        RelationshipType.relatedToType.searchable = true
        RelationshipType.relatedToType.save()
        relationshipTypeService.clearCache()

        elasticSearchService.reindex(false).toBlocking().subscribe()
    }


    @IgnoreIf( { System.getProperty('spock.ignore.slow') })
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

    @IgnoreIf( { System.getProperty('spock.ignore.slow') })
    def "test import MET-523"() {
        when:
        catalogueBuilder.build {
            skip draft
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
        elasticSearchService.reindex(true).toBlocking().last()

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
