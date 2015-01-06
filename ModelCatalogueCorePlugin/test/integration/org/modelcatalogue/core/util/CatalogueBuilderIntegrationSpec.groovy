package org.modelcatalogue.core.util

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.CatalogueBuilder

class CatalogueBuilderIntegrationSpec extends IntegrationSpec {

    def initCatalogueService
    def classificationService
    def elementService

    Set<CatalogueElement> created = []

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
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
        Classification.findByModelCatalogueId('http://www.w3.org/2001/TestSchema')
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

    def "reuse existing classification by id"() {
        Classification c = new Classification(name: 'SchemaWithId', modelCatalogueId: 'http://www.example.com/SWI').save(failOnError: true)

        build {
            classification(name: 'NotUniqueName', id: 'http://www.example.com/SWI') {
                description '''
                    This is a test schema which is just for test purposes!
                '''
            }
        }

        expect:
        created.first() == c
        created.first().name == 'NotUniqueName'
    }

    def "complain if classification name is missing"() {
        when:
        build {
            classification namespace: 'http://www.w3.org/2001/TestSchema'
        }

        then:
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message.startsWith "Cannot create element abstraction from"
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

    def "creates only one measurement unit as measurement unit name is unique"() {
        MeasurementUnit unit = new MeasurementUnit(name: 'ExistingUnit2', status: ElementStatus.DEPRECATED).save(failOnError: true, flush: true)

        build {
            classification(name: "TestClassificationA") {
                measurementUnit(name: 'ExistingUnit2', symbol: 'EU2') {
                    description '''
                        This is a test unit which is just for test purposes!
                    '''
                }
            }
            classification(name: "TestClassificationB") {
                measurementUnit(name: 'ExistingUnit2', symbol: 'EU2') {
                    description '''
                        This is a test unit which is just for test purposes!
                    '''
                }
            }
        }

        expect:
        created.find { it instanceof  MeasurementUnit} == unit
        created.size() == 3
        created.any { it.name == 'TestClassificationA'}
        created.any { it.name == 'TestClassificationB'}
        created.any { it.name == 'ExistingUnit2'}
    }

    def "complain if measurement unit name is missing"() {
        when:
        build {
            measurementUnit symbol: 'TU'
        }

        then:
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message.startsWith "Cannot create element abstraction from"
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
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message.startsWith "Cannot create element abstraction from"
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
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message.startsWith "Cannot create element abstraction from"
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
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message.startsWith "Cannot create element abstraction from"
    }

    def "specify rule as regex"() {
        when:
        build {
            valueDomain name: 'with regex', {
                regex(/\w+/)
            }
        }

        ValueDomain domain = ValueDomain.findByName('with regex')

        then:
        domain
        domain.rule == """x ==~ /\\w+/"""
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
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message.startsWith "Cannot create element abstraction from"
    }

    def "do not complain if data type name is missing but inside value domain"() {
        build {
            valueDomain(name: 'test:number') {
                dataType()
            }
        }

        expect:
        DataType.findByName('test:number')
        ValueDomain.findByName('test:number')
        ValueDomain.findByName('test:number').dataType
        ValueDomain.findByName('test:number').dataType == DataType.findByName('test:number')
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

    def "create generic relationship"() {
        build {
            classification name: "Other123", {
                valueDomain name: 'WD40'
            }
            classification name: "Other234", {
                valueDomain name: 'VDRel1'
                def vd2 = valueDomain name: 'VDRel2'
                valueDomain name: 'VDRel3'
                valueDomain name: 'VDRel4', {
                    rel 'synonym'   to      vd2
                    rel 'synonym'   from    'VDRel1'
                    rel 'relatedTo' to      'Other123', 'WD40'
                    rel 'base'      to      'Other123', 'WD40'
                }
            }
        }

        expect:
        ValueDomain.findByName('VDRel4')
        ValueDomain.findByName('VDRel4').countRelationsByType(RelationshipType.findByName('synonym'))   == 2
        ValueDomain.findByName('VDRel4').countRelationsByType(RelationshipType.findByName('base'))      == 1
        ValueDomain.findByName('VDRel4').countRelationsByType(RelationshipType.findByName('relatedTo')) == 1
    }

    def "creates new version of the element"() {
        build {
            classification(name: "NewVersion1") {
                // creates finalized model
                model name: "ModelNV1", id: "http://www.example.com/models/ModelNV1", {
                    status finalized
                }
            }
            classification(name: "NewVersion2") {
                model name: "ModelNVX1", id: "http://www.example.com/models/ModelNVX1", {
                    status finalized
                }
            }
        }

        expect:
        Model.findByName("ModelNV1")?.status == ElementStatus.FINALIZED

        when:
        build {
            classification(name: "NewVersion1") {
                // this should create new version with different name
                model name: "ModelNV2", id: "http://www.example.com/models/ModelNV1"
            }
        }


        then: "new model is draft"
        Model.findByName("ModelNV2")?.status            == ElementStatus.DRAFT
        Model.findByName("ModelNV2")?.modelCatalogueId  == "http://www.example.com/models/ModelNV1"
        Model.findByName("ModelNV2")?.latestVersionId   == Model.findByName("ModelNV1")?.id

        and: "the old model is still finalized"
        Model.findByName("ModelNV1")?.status            == ElementStatus.FINALIZED
        Model.findByName("ModelNV1")?.modelCatalogueId  == "http://www.example.com/models/ModelNV1"


    }

    private void build(@DelegatesTo(CatalogueBuilder) Closure cl) {
        created = new CatalogueBuilder(classificationService, elementService).build cl
    }


}
