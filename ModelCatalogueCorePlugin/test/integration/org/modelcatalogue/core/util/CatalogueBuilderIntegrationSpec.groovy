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

    def "reuse existing classification with by name"() {
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
        e.message.startsWith "You must provide the name of the classification"
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
        ValueDomain.countByName('SomeDomain') == 3

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
        e.message.startsWith "You must provide the name of the value domain"
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
        DataType.countByName('SomeType') == 3

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
        e.message.startsWith "You must provide the name of the data type"
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


    private void build(@DelegatesTo(CatalogueBuilder) Closure cl) {
        created = new CatalogueBuilder(classificationService).build cl
    }


}
