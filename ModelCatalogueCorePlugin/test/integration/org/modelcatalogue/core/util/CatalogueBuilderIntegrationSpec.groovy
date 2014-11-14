package org.modelcatalogue.core.util

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.*

class CatalogueBuilderIntegrationSpec extends IntegrationSpec {

    def initCatalogueService
    def classificationService

    Set<CatalogueElement> created = []

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    def cleanup() {
        created.each {
            if (!it.readyForQueries) {
                it.attach()
                return
            }
            [it.outgoingRelations, it.incomingRelations, it.outgoingMappings, it.incomingMappings].each { col ->
                col?.each { rel ->
                    rel.delete()
                }
            }
            it.delete()
        }
    }
    
    def "creates new classification with given name, namespace and description"() {
        build {
            classification(name: 'TestSchema', namespace: 'http://www.w3.org/2001/TestSchema') {
                description '''
                    This is a test schema which is just for test purposes!
                '''
            }
        }

        expect:
        Classification.findByName('TestSchema')
        Classification.findByName('TestSchema').description == 'This is a test schema which is just for test purposes!'
        Classification.findByNamespace('http://www.w3.org/2001/TestSchema')
    }

    def "reuse existing classification by name"() {
        Classification c = new Classification(name: 'ExistingSchema', status: ElementStatus.DEPRECATED).save(failOnError: true)

        build {
            classification(name: 'ExistingSchema', namespace: 'http://www.w3.org/2001/ExistingSchema') {
                description '''
                    This is a test schema which is just for test purposes!
                '''
            }
        }

        expect:
        created.first() == c
    }

    def "complain if classification name is missing"() {
        when:
        build {
            classification namespace: 'http://www.w3.org/2001/TestSchema'
        }

        then:
        AssertionError e = thrown(AssertionError)
        e.message.startsWith "You must provide the name of the Classification"
    }
    
    def "creates new measurement unit with given name"() {
        build {
            measurementUnit(name: 'TestUnit', symbol: 'TU') {
                description '''
                    This is a test unit which is just for test purposes!
                '''
            }
        }

        expect:
        MeasurementUnit.findByName('TestUnit')
        MeasurementUnit.findByName('TestUnit').description == 'This is a test unit which is just for test purposes!'
    }

    def "reuse existing measurement unit by name"() {
        MeasurementUnit unit = new MeasurementUnit(name: 'ExistingUnit', status: ElementStatus.DEPRECATED).save(failOnError: true)

        build {
            measurementUnit(name: 'ExistingUnit', symbol: 'EU') {
                description '''
                    This is a test unit which is just for test purposes!
                '''
            }
        }

        expect:
        created.first() == unit
    }

    def "complain if measurement unit name is missing"() {
        when:
        build {
            measurementUnit symbol: 'TU'
        }

        then:
        AssertionError e = thrown(AssertionError)
        e.message.startsWith "You must provide the name of the Measurement Unit"
    }

    def "creates new data element with given name"() {
        build {
            dataElement(name: 'TestElement') {
                description '''
                    This is a test element which is just for test purposes!
                '''
            }
        }

        expect:
        DataElement.findByName('TestElement')
        DataElement.findByName('TestElement').description == 'This is a test element which is just for test purposes!'
    }

    def "reuse existing data element unit by name"() {
        DataElement unit = new DataElement(name: 'ExistingElement', status: ElementStatus.DEPRECATED).save(failOnError: true)

        build {
            dataElement(name: 'ExistingElement') {
                description '''
                    This is a test element which is just for test purposes!
                '''
            }
        }

        expect:
        created.first() == unit
    }

    def "complain if data element name is missing"() {
        when:
        build {
            dataElement([:])
        }

        then:
        AssertionError e = thrown(AssertionError)
        e.message.startsWith "You must provide the name of the Data Element"
    }

    def "creates new model with given name"() {
        build {
            model(name: 'TestModel') {
                description '''
                    This is a test model which is just for test purposes!
                '''
            }
        }

        expect:
        Model.findByName('TestModel')
        Model.findByName('TestModel').description == 'This is a test model which is just for test purposes!'
    }

    def "reuse existing model by name"() {
        Model unit = new Model(name: 'ExistingModel', status: ElementStatus.DEPRECATED).save(failOnError: true)

        build {
            model(name: 'ExistingModel') {
                description '''
                    This is a test model which is just for test purposes!
                '''
            }
        }

        expect:
        created.first() == unit
    }

    def "complain if model name is missing"() {
        when:
        build {
            model([:])
        }

        then:
        AssertionError e = thrown(AssertionError)
        e.message.startsWith "You must provide the name of the Model"
    }

    def "creates new value domain with given name"() {
        build {
            valueDomain name: 'Domain'
        }

        expect:
        ValueDomain.findByName('Domain')
    }

    def "reuses existing value domain with given name in classification"() {
        Classification cls  = new Classification(name: 'Some').save(failOnError: true)
        ValueDomain domain1 = new ValueDomain(name: 'SomeDomain').save(failOnError: true)
        ValueDomain domain2 = new ValueDomain(name: 'SomeDomain').save(failOnError: true)

        cls.addToClassifies(domain1)

        build {
            valueDomain name: 'SomeDomain'
            classification(name: 'Some') {
                valueDomain name: 'SomeDomain'
            }
        }

        expect:
        ValueDomain.countByName('SomeDomain') == 2

        cleanup:
        [domain2].each {
            it.beforeDelete()
            it.delete()
        }
    }

    def "complain if value domain name is missing"() {
        when:
        build {
            valueDomain rule: 'is number'
        }

        then:
        AssertionError e = thrown(AssertionError)
        e.message.startsWith "You must provide the name of the Value Domain"
    }

    def "add extensions"() {
        build {
            dataType name: 'test:blah', {
                ext 'Local Identifier', 'BLAH'
                ext Alias: 'test:TYPE'
                ext foo: 'bar'
            }
        }

        when:
        DataType blah = DataType.findByName('test:blah')

        then:
        blah
        blah.ext.size() == 3
        blah.ext.containsKey('Local Identifier')
        blah.ext.containsKey('Alias')
        blah.ext.containsKey('foo')
    }

    def "creates new data type with given name"() {
        build {
            dataType name: 'Type'
        }

        expect:
        DataType.findByName('Type')
    }

    def "reuses existing data type with given name"() {
        Classification cls  = new Classification(name: 'Some').save(failOnError: true)
        DataType dt1 = new DataType(name: 'SomeType').save(failOnError: true)
        DataType dt2 = new DataType(name: 'SomeType').save(failOnError: true)

        cls.addToClassifies(dt1)

        build {
            dataType name: 'SomeType'
            classification(name: 'Some') {
                dataType name: 'SomeType'
            }
        }

        expect:
        DataType.countByName('SomeType') == 2

        cleanup:
        [dt2].each {
            it.beforeDelete()
            it.delete()
        }
    }

    def "complain if data type name is missing"() {
        when:
        build {
            dataType status: ElementStatus.FINALIZED
        }

        then:
        AssertionError e = thrown(AssertionError)
        e.message.startsWith "You must provide the name of the Data Type"
    }

    def "do not complain if data type name is missing but inside value domain"() {
        build {
            valueDomain(name: 'test:number') {
                dataType()
            }
        }

        expect:
        DataType.findByName('test:number')
    }



    def "elements are added to classification"() {
        build {
            classification(name: 'TestSchema') {
                valueDomain(name: 'test:string domain') {
                    dataType(name: 'test:string')
                }
                valueDomain(name: 'test:token domain') {
                    basedOn 'test:string domain'
                }
            }
        }

        Classification schema       = Classification.findByName('TestSchema')
        ValueDomain stringDomain    = ValueDomain.findByName('test:string domain')
        ValueDomain tokenDomain     = ValueDomain.findByName('test:token domain')
        DataType stringType         = DataType.findByName('test:string')

        expect:
        schema
        stringDomain
        tokenDomain
        stringType

        schema.classifies
        stringDomain    in schema.classifies
        tokenDomain     in schema.classifies
        stringType      in schema.classifies

        stringDomain.dataType == stringType

        tokenDomain.isBasedOn
        tokenDomain.isBasedOn.contains stringDomain

    }

    def "complex model"() {
        build {
            automatic valueDomain
            automatic dataType

            classification name: 'Complex', {
                id 'http://www.example.com/complex-model'

                model name: "Complex Grand Parent", {
                    model name: "Complex Parent", {
                        model name: "Complex Child", {
                            dataElement name: "Complex Element 1"
                            dataElement name: "Complex Element 2", {
                                valueDomain name: "Complex Domain 2", {
                                    dataType enumerations: [yes: 'Yes', no: 'No']
                                    measurementUnit name: 'Unit'
                                }
                            }
                        }
                    }

                    model name: "Complex Sibling", {
                        dataElement name: "Sibling Element"
                    }
                }
            }
        }

        expect:
        Model.findByName('Complex Grand Parent')
        Model.findByName('Complex Grand Parent').parentOf
        Model.findByName('Complex Grand Parent').parentOf.size() == 2

        Model.findByName('Complex Child')
        Model.findByName('Complex Child').contains
        Model.findByName('Complex Child').contains.size() == 2

        ValueDomain.findByName('Complex Element 1')
    }


    private void build(@DelegatesTo(CatalogueBuilder) Closure cl) {
        created = new CatalogueBuilder(classificationService).build cl
    }


}
