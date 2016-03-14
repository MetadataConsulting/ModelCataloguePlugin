package org.modelcatalogue.core.util

import grails.util.Holders
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import spock.lang.Issue

import static org.modelcatalogue.core.util.HibernateHelper.*

class CatalogueBuilderIntegrationSpec extends AbstractIntegrationSpec {

    def dataModelService
    def elementService

    Set<CatalogueElement> created = []

    def setup() {
        loadMarshallers()
        initRelationshipTypes()
    }

    def "creates new classification with given name, namespace and description"() {
        build {
            dataModel(name: 'TestSchema', namespace: 'http://www.w3.org/2001/TestSchema') {
                description '''
                    This is a test schema which is just for test purposes!
                '''
            }
        }

        expect:
        DataModel.findByName('TestSchema')
        DataModel.findByName('TestSchema').description == 'This is a test schema which is just for test purposes!'
        DataModel.findByModelCatalogueId('http://www.w3.org/2001/TestSchema')
    }

    def "reuse existing classification by name"() {
        DataModel c = new DataModel(name: 'ExistingSchema', status: ElementStatus.DEPRECATED).save(failOnError: true)

        build {
            dataModel(name: 'ExistingSchema', namespace: 'http://www.w3.org/2001/ExistingSchema') {
                description '''
                    This is a test schema which is just for test purposes!
                '''
            }
        }

        expect:
        created.first().latestVersionId == c.refresh().latestVersionId
    }

    def "reuse existing classification by id"() {
        DataModel c = new DataModel(name: 'SchemaWithId', modelCatalogueId: 'http://www.example.com/SWI').save(failOnError: true)

        build {
            dataModel(name: 'NotUniqueName', id: 'http://www.example.com/SWI') {
                description '''
                    This is a test schema which is just for test purposes!
                '''
            }
        }

        DataModel updated = DataModel.findByName('NotUniqueName')

        expect:
        updated
        updated.status == ElementStatus.DRAFT
        updated.latestVersionId == c.id
    }

    def "complain if classification name is missing"() {
        when:
        build {
            dataModel namespace: 'http://www.w3.org/2001/TestSchema'
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
        created.first().latestVersionId == unit.refresh().latestVersionId
    }

    def "creates only one measurement unit as measurement unit name is unique"() {
        new MeasurementUnit(name: 'ExistingUnit2', status: ElementStatus.DEPRECATED).save(failOnError: true, flush: true)

        when:
        build {
            dataModel(name: "TestClassificationA") {
                measurementUnit(name: 'ExistingUnit2', symbol: 'EU2') {
                    description '''
                        This is a test unit which is just for test purposes!
                    '''
                }
            }
            dataModel(name: "TestClassificationB") {
                measurementUnit(name: 'ExistingUnit2', symbol: 'EU2') {
                    description '''
                        This is a test unit which is just for test purposes!
                    '''
                }
            }
        }

        then:
        MeasurementUnit.countByName('ExistingUnit2') == 1
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
        created.first().latestVersionId == unit.refresh().latestVersionId
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
            dataClass(name: 'TestModel') {
                description '''
                    This is a test model which is just for test purposes!
                '''
            }
        }

        expect:
        DataClass.findByName('TestModel')
        DataClass.findByName('TestModel').description == 'This is a test model which is just for test purposes!'
    }

    def "reuse existing model by name"() {
        DataClass unit = new DataClass(name: 'ExistingModel', status: ElementStatus.DEPRECATED).save(failOnError: true)

        build {
            dataClass(name: 'ExistingModel') {
                description '''
                    This is a test model which is just for test purposes!
                '''
            }
        }

        expect:
        created.first().latestVersionId == unit.refresh().latestVersionId
    }

    def "complain if model name is missing"() {
        when:
        build {
            dataClass([:])
        }

        then:
        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message.startsWith "Cannot create element abstraction from"
    }

    def "value domain calls are delegated to the data type"() {
        build {
            dataType name: 'Domain'
        }

        expect:
        DataType.findByName('Domain')
    }

    def "if there is a nested data type with name missing - it is merged into single data type"() {
        build {
            dataType name: 'Merged Type', {
                dataType(description: 'MT DESC')
            }
        }

        expect:
        DataType.findByName('Merged Type')
        DataType.findByName('Merged Type').description == 'MT DESC'
    }


    def "if there is a nested data type with different name - it's set as base"() {
        build {
            dataType name: 'Inherited Type', {
                dataType(name: 'Base Type', description: 'MT DESC')
            }
        }

        expect:
        DataType.findByName('Inherited Type')
        DataType.findByName('Base Type')

        DataType.findByName('Inherited Type').isBasedOn
        DataType.findByName('Inherited Type').isBasedOn.contains DataType.findByName('Base Type')
    }



    def "specify rule as regex"() {
        when:
        build {
            dataType name: 'with regex', {
                regex(/\w+/)
            }
        }

        DataType dataType = DataType.findByName('with regex')

        then:
        dataType
        dataType.rule == """x ==~ /\\w+/"""
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
        DataModel cls  = new DataModel(name: 'Some').save(failOnError: true)
        new DataType(name: 'SomeType', dataModel: cls).save(failOnError: true)
        DataType dt2 = new DataType(name: 'SomeType').save(failOnError: true)

        build {
            dataType name: 'SomeType'
            dataModel(name: 'Some') {
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

    def "if enumerations present, create enumerated type"() {
        build {
            dataType name: 'ETTest', enumerations: [foo: 'bar']
        }

        expect:
        EnumeratedType.findByName('ETTest')
    }


    def "if data class present, create reference type"() {
        build {
            dataType name: 'RTTest', {
                dataClass name: 'RTTest DC'
            }
        }

        ReferenceType referenceType = ReferenceType.findByName('RTTest')

        expect:
        referenceType
        referenceType.dataClass
        referenceType.dataClass.name == "RTTest DC"
    }


    def "if unit of measure present, create primitive type"() {
        build {
            dataType name: 'PTTest', {
                measurementUnit name: 'PTUnit'
            }
        }

        PrimitiveType primitiveType = PrimitiveType.findByName('PTTest')

        expect:
        primitiveType
        primitiveType.measurementUnit
        primitiveType.measurementUnit.name == "PTUnit"
    }

    def "do not complain if data type name is missing but inside data element"() {
        build {
            dataElement(name: 'test:number') {
                dataType()
            }
        }

        expect:
        DataType.findByName('test:number')
        DataElement.findByName('test:number')
        DataElement.findByName('test:number').dataType
        DataElement.findByName('test:number').dataType == DataType.findByName('test:number')
    }



    def "elements are added to classification"() {
        build {
            dataModel(name: 'TestSchema') {
                dataType(name: 'test:string')
                dataType(name: 'test:token') {
                    basedOn 'test:string'
                }
            }
        }

        DataModel schema       = DataModel.findByName('TestSchema')
        DataType tokenType     = DataType.findByName('test:token')
        DataType stringType    = DataType.findByName('test:string')

        expect:
        schema
        tokenType
        stringType

        schema.declares
        tokenType in schema.declares
        stringType in schema.declares

        tokenType.isBasedOn
        tokenType.isBasedOn.contains stringType

    }

    def "complex model"() {
        build {
            automatic dataType

            dataModel name: 'Complex', semanticVersion: '1.0.0', {
                id 'http://www.example.com/complex-model'

                revisionNotes 'This is a brand new model'

                dataClass name: "Complex Grand Parent", {
                    dataClass name: "Complex Parent", {
                        dataClass name: "Complex Child", {
                            dataElement name: "Complex Element 1"
                            dataElement name: "Complex Element 2", {
                                dataType enumerations: [yes: 'Yes', no: 'No']
                            }
                        }
                    }

                    dataClass name: "Complex Sibling", {
                        dataElement name: "Sibling Element"
                    }
                }
            }
        }

        expect:
        DataClass.findByName('Complex Grand Parent')
        DataClass.findByName('Complex Grand Parent').parentOf
        DataClass.findByName('Complex Grand Parent').parentOf.size() == 2

        DataClass.findByName('Complex Child')
        DataClass.findByName('Complex Child').contains
        DataClass.findByName('Complex Child').contains.size() == 2

        DataModel.findByName('Complex').semanticVersion == '1.0.0'
        DataModel.findByName('Complex').revisionNotes == 'This is a brand new model'
    }

    def "create generic relationship"() {
        final String vd1Name = 'VDRel1'
        final String vd2Name = 'VDRel2'
        final String vd3Name = 'VDRel3'
        final String vd4Name = 'VDRel4'
        final String other123Name = 'Other123'
        final String other234Name = 'Other234'
        final String wd40name = 'WD40'


        build {
            dataModel name: other123Name, {
                dataType name: wd40name
            }
            dataModel name: other234Name, {
                dataType name: vd1Name
                dataType name: vd2Name
                dataType name: vd3Name
                dataType name: vd4Name, {
                    rel 'synonym'   to      dataType called vd2Name
                    rel 'synonym'   from    vd1Name


                    rel 'relatedTo' to      other123Name, wd40name
                    rel 'base'      to      other123Name, wd40name
                }
            }
        }

        expect:
        RelationshipType.synonymType.bidirectional
        RelationshipType.relatedToType.bidirectional
        !RelationshipType.baseType.bidirectional

        when:
        DataType vd1 = DataType.findByName(vd1Name)
        DataType vd2 = DataType.findByName(vd2Name)
        DataType vd4 = DataType.findByName(vd4Name)

        DataType wd40 = DataType.findByName(wd40name)

        then:
        vd1
        vd2
        vd4
        wd40

        vd2 in vd4.isSynonymFor
        vd4 in vd2.isSynonymFor

        vd1 in vd4.isSynonymFor
        vd4 in vd1.isSynonymFor

        vd4 in wd40.relatedTo
        wd40 in vd4.relatedTo

        vd4 in wd40.isBasedOn
        wd40 in vd4.isBaseFor
    }

    def "creates new version of the element"() {
        build {
            dataModel(name: "NewVersion1") {
                // creates finalized model
                dataClass name: "ModelNV1", id: "http://www.example.com/models/ModelNV1"
            }
        }

        created.each {
            assert it.publish(elementService).errors.errorCount == 0
        }

        build {
            dataModel(name: "NewVersion2") {
                dataClass name: "ModelNVX1", id: "http://www.example.com/models/ModelNVX1"
            }
        }

        expect:
        DataClass.findByName("ModelNV1")?.status == ElementStatus.FINALIZED

        when:
        build {
            dataModel(name: "NewVersion1") {
                // this should create new version with different name
                dataClass name: "ModelNV2", id: "http://www.example.com/models/ModelNV1"
            }
        }


        then: "new model is draft"
        DataClass.findByName("ModelNV2")?.modelCatalogueId  == "http://www.example.com/models/ModelNV1"
        DataClass.findByName("ModelNV2")?.latestVersionId   == DataClass.findByName("ModelNV1")?.id
        DataClass.findByName("ModelNV2")?.status            == ElementStatus.DRAFT
        DataClass.findByName("ModelNV2")?.dataModel
        DataClass.findByName("ModelNV2")?.dataModel?.status == ElementStatus.DRAFT
        DataClass.findByName("ModelNV2")?.dataModel?.name   == 'NewVersion1'


        and: "the old model is still finalized"
        DataClass.findByName("ModelNV1")?.status            == ElementStatus.FINALIZED
        DataClass.findByName("ModelNV1")?.modelCatalogueId  == "http://www.example.com/models/ModelNV1"

        and: "there are two NewVersion1 classifications at the moment"
        DataModel.countByName('NewVersion1')                                   == 2
        DataModel.countByNameAndStatus('NewVersion1', ElementStatus.DRAFT)     == 1
        DataModel.countByNameAndStatus('NewVersion1', ElementStatus.FINALIZED) == 1


    }

    def "adds metadata to nested relationship like child model"() {
        build {
            dataModel(name: "AMTNRLC") {
                dataClass(name: "Parent 007") {
                    dataClass(name: "Child 008") {
                        relationship {
                            ext "Min. Occurs", "1"
                        }
                    }
                }
            }
        }

        when:
        Relationship rel = Relationship.findBySourceAndDestination(DataClass.findByName('Parent 007'), DataClass.findByName('Child 008'))

        then:
        rel
        rel.ext
        rel.ext.size() == 1
        rel.ext['Min. Occurs'] == '1'


        when:
        elementService.finalizeElement(DataClass.findByName('Parent 007'))


        then:
        DataClass.findByName('Parent 007').status == ElementStatus.FINALIZED

        when:
        build {
            dataModel(name: "AMTNRLC") {
                dataClass(name: "Parent 007") {
                    dataClass(name: "Child 008") {
                        relationship {
                            ext "Min. Occurs", "0"
                        }
                    }
                }
            }
        }

        then:
        DataClass.findByName('Parent 007', [sort: 'versionNumber', order: 'desc']).status == ElementStatus.DRAFT
    }


    def "migrates hierarchy relationship to new draft version"() {
        build {
            dataModel(name: 'MHR MODEL') {
                dataClass(name: 'MHR ROOT') {
                    dataClass(name: 'MHR L1') {
                        dataClass(name: 'MHR L2') {
                            dataClass(name: 'MHR L3')
                        }
                    }
                }
            }
        }

        DataModel dataModel = elementService.finalizeElement(DataModel.findByName('MHR MODEL'))

        DataClass l1Finalized = DataClass.findByName('MHR L1')
        DataClass l3Finalized = DataClass.findByName('MHR L3')

        expect:
        l1Finalized
        l1Finalized.status == ElementStatus.FINALIZED
        l1Finalized.parentOf.size() == 1

        l3Finalized
        l3Finalized.childOf.size() == 1

        when:
        DraftContext context = DraftContext.userFriendly()
        elementService.createDraftVersion(dataModel, '1.0.0', context)
        DataClass l1Draft = context.resolve(l1Finalized) as DataClass

        then:
        l1Draft
        l1Draft.status == ElementStatus.DRAFT
        l1Draft.parentOf.size() == 1

        when:
        DataClass l3Draft = context.resolve(l3Finalized)

        then:
        l3Draft
        l3Draft.status == ElementStatus.DRAFT
        l3Draft.childOf.size()  == 1
        l1Draft.parentOf.size() == 1
    }

    private void build(@DelegatesTo(CatalogueBuilder) Closure cl) {
        DefaultCatalogueBuilder defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
        defaultCatalogueBuilder.build cl
        created = defaultCatalogueBuilder.created
    }


    def "order from builder is persisted"() {
        when:
        build {
            dataModel (name: 'Order Test') {
                dataClass (name: 'OT Parent') {
                    dataElement (name: 'OT Child 002')
                    dataElement (name: 'OT Child 001')
                    dataElement (name: 'OT Child 004')
                    dataElement (name: 'OT Child 003')
                }
            }
        }

        then:
        DataClass.findByName('OT Parent').contains*.name == [
                'OT Child 002',
                'OT Child 001',
                'OT Child 004',
                'OT Child 003'
        ]

        when:
        build {
            dataModel (name: 'Order Test') {
                dataClass (name: 'OT Parent') {
                    dataElement (name: 'OT Child 001')
                    dataElement (name: 'OT Child 002')
                    dataElement (name: 'OT Child 003')
                    dataElement (name: 'OT Child 004')
                }
            }
        }

        then:
        DataClass.findByName('OT Parent').contains*.name == [
                'OT Child 001',
                'OT Child 002',
                'OT Child 003',
                'OT Child 004'
        ]
    }

    def "define id as closure"() {
        Object old = Holders.grailsApplication.config.grails.serverURL
        Holders.grailsApplication.config.grails.serverURL = "http://localhost:8080/ModelCatalogueCorePluginTestApp"
        build {
            dataModel(name: 'CS4ID') {
                id { String name, Class type ->
                    "http://www.example.com/classification/${type.simpleName[0].toLowerCase()}/$name"
                }
                dataClass(name: "Model_ID")
            }
        }

        DataClass model = DataClass.findByName('Model_ID')

        expect:
        model.modelCatalogueId == "http://www.example.com/classification/d/Model_ID"

        cleanup:
        Holders.grailsApplication.config.grails.serverURL = old
    }

    @Issue("MET-587")
    def "remove child model when missing"() {
        when:
        build {
            dataModel(name: "C4RMC") {
                dataClass (name: 'C4RMC Parent') {
                    dataClass (name: 'C4RMC Child 1')
                    dataClass (name: 'C4RMC Child 2')
                    dataClass (name: 'C4RMC Child 3')
                }
            }
        }

        DataModel c4rmc = DataModel.findByName('C4RMC')
        DataClass c4rmcParent = DataClass.findByName('C4RMC Parent')
        then:
        c4rmc
        c4rmcParent
        c4rmcParent.countParentOf() == 3

        when: "model is removed from finalized element"
        elementService.finalizeElement(c4rmc)
        c4rmc.status == ElementStatus.FINALIZED

        build {
            dataModel(name: "C4RMC") {
                dataClass (name: 'C4RMC Parent') {
                    dataClass (name: 'C4RMC Child 2')
                    dataClass (name: 'C4RMC Child 3')
                }
            }
        }

        DataClass draft = DataClass.findByNameAndStatus('C4RMC Parent', ElementStatus.DRAFT)

        then: "creation of draft should be triggered for finalized element"
        draft
        draft.countParentOf() == 2

        when: "model is removed from draft element"
        elementService.finalizeElement(c4rmc)
        c4rmc.status == ElementStatus.FINALIZED

        build {
            dataModel(name: "C4RMC") {
                dataClass (name: 'C4RMC Parent') {
                    dataClass (name: 'C4RMC Child 3')
                }
            }
        }

        draft = DataClass.findByNameAndStatus('C4RMC Parent', ElementStatus.DRAFT)

        then: "the relationship should be removed from the element"
        draft
        draft.countParentOf() == 1
    }

    @Issue("MET-620")
    def "finalizes model after creation"() {
        when:
        build {
            dataClass(name: 'Parent Model 4 Finalization') {
                status finalized
                dataClass(name: 'Child Model 4 Finalization')
            }
        }
        then:
        noExceptionThrown()
        DataClass.findByName('Parent Model 4 Finalization').status == ElementStatus.FINALIZED
        DataClass.findByName('Child Model 4 Finalization').status == ElementStatus.FINALIZED
    }

    def "can call relationship closure more than once"() {
        String name = 'Model for Double Relationship Call'
        when:
        build {
            dataClass(name: name) {
                dataClass(name: "$name Child") {
                    relationship {
                        ext 'one', '1'
                    }
                    relationship {
                        archived = true
                    }
                }
            }
        }

        DataClass parent = created.find { it.name == name }

        then:
        parent
        parent.parentOfRelationships
        parent.parentOfRelationships.size() == 1
        parent.parentOfRelationships[0].archived
        parent.parentOfRelationships[0].ext['one'] == '1'
    }

    def "don't get misconfigured from other relationship calls"() {
        String name = 'Model for Misconfigured Relationship Call'
        String ch1Name = "$name Child 1"
        String ch2Name = "$name Child 2"
        when:
        build {
            dataClass(name: name) {
                dataClass(name: ch1Name) {
                    relationship {
                        ext 'one', '1'
                        ext 'two', '2'
                    }
                }

                dataClass(name: ch2Name) {
                    relationship {
                        ext 'two', 'II'
                        ext 'three', 'III'
                    }
                }
            }
        }
        DataClass parent = created.find { it.name == name } as DataClass

        then:
        parent

        when:
        Relationship ch1 = parent.outgoingRelationships.find { it.destination.name == ch1Name }
        Relationship ch2 = parent.outgoingRelationships.find { it.destination.name == ch2Name }

        then:
        ch1
        ch1.ext.one == '1'
        ch1.ext.two == '2'
        ch2
        ch2.ext.two == 'II'
        ch2.ext.three == 'III'
    }

    def "should be able to copy relationships where the classification is finalized"() {
        build {
            dataModel(name: 'C4CR', status: finalized)
        }
        when:
        build {
            // copy relationships
            dataModel(name: 'C4CR') {
                dataClass(name: 'C4CR GP') {
                    dataClass(name: 'C4CR P') {
                        dataClass(name: 'C4CR C1')
                        dataClass(name: 'C4CR C2')
                        dataClass(name: 'C4CR C3')
                    }
                }
            }
        }

        then:
        noExceptionThrown()
        CatalogueElement.findByNameAndStatus('C4CR GP', ElementStatus.DRAFT).dataModel?.name == 'C4CR'
    }


    def "should be able to copy relationships where the model is finalized"() {
        build {
            dataModel(name: 'C4CR2', status: finalized) {
                dataClass(name: 'C4CR2 GP', status: finalized)
            }
        }
        when:
        build {
            copy relationships
            dataModel(name: 'C4CR2') {
                dataClass(name: 'C4CR2 GP') {
                    dataClass(name: 'C4CR2 P') {
                        dataClass(name: 'C4CR2 C1')
                        dataClass(name: 'C4CR2 C2')
                        dataClass(name: 'C4CR2 C3')
                    }
                }
            }
        }

        then:
        noExceptionThrown()
        CatalogueElement.findByName('C4CR2 P').parentOf.size() == 3
        CatalogueElement.findByName('C4CR2 P').childOf.any { it.name == 'C4CR2 GP'}
        CatalogueElement.findByNameAndStatus('C4CR2 GP', ElementStatus.DRAFT).parentOf.any { it.name == 'C4CR2 P'}
    }


    def "can change data type's type"() {
        when:
        build {
            dataModel name: 'DM4CCDTT', {
                dataType name: 'DT4CCDTT'
            }
        }

        DataType type = DataType.findByName 'DT4CCDTT'

        then:
        type
        type.instanceOf(DataType)
        !type.instanceOf(PrimitiveType)
        getEntityClass(type) == DataType


        when:
        build {
            dataModel name: 'DM4CCDTT', {
                dataType name: 'DT4CCDTT', {
                    measurementUnit name: 'MU4CCDTT'
                }
            }
        }

        type = DataType.findByName('DT4CCDTT', [order: 'desc', sort: 'versionNumber'])

        then:
        type
        type.instanceOf(DataType)
        type.instanceOf(PrimitiveType)
        getEntityClass(type) == PrimitiveType

    }


    def "ignores missing references"() {
        final String assetModelName = 'Asset Model'
        final String testAssetName = 'Test Asset'
        final String dataClassName = 'Test Data Class'
        final String valueDomainName = 'ValueDomain IMR'
        final String valueDomainId1 = 'http://www.example.com/200'
        final String valueDomainId2 = 'http://www.example.com/201'


        User admin = User.findByName('admin') ?: new User(name: 'admin', username: 'admin', enabled: true, password: 'admin').save(failOnError: true)
        DataModel assetDataModel = new DataModel(name: assetModelName).save(failOnError: true)
        Asset asset = new Asset(dataModel: assetDataModel, name: testAssetName).save(failOnError: true)

        expect:
        admin
        asset.dataModel == assetDataModel
        asset in assetDataModel.declares

        when:
        build {
            dataModel name: assetModelName, {
                dataClass name: dataClassName, {
                    rel 'favourite' from(ref(admin.getDefaultModelCatalogueId(true)))
                    rel 'relatedTo' from(ref(asset.getDefaultModelCatalogueId(true)))
                }
            }
        }
        then:
        noExceptionThrown()

        when:
        build {
            dataModel name: assetModelName, {
                rel 'classificationFilter' to(ref(admin.getDefaultModelCatalogueId(true))) {}
                dataClass name: dataClassName, {
                    rel 'synonym' from ref('http://www.example.com/100.1')
                    rel 'synonym' from 'Foo', 'Bar'
                }
                dataType name:  valueDomainName, id: valueDomainId1, dataModel: 'Some other model', {
                    dataType name: valueDomainName, id: valueDomainId2, enumerations: [foo: 'bar']
                }
            }
        }
        then:
        noExceptionThrown()

    }


    def "data models in paramters will create new data models"() {
        final String parentDataClassName = 'Parent DMIP'
        final String childDataClassName = 'Child DMIP'
        final String modelOneName = 'Model #001'
        final String modelTwoName = 'Model #002'

        build {
            dataClass name: parentDataClassName, dataModel: modelOneName, {
                dataClass name: childDataClassName, classification: modelTwoName
            }
        }

        DataModel modelOne = DataModel.findByName(modelOneName)
        DataModel modelTwo = DataModel.findByName(modelTwoName)
        DataClass parentDataClass = DataClass.findByName(parentDataClassName)
        DataClass childDataClass = DataClass.findByName(childDataClassName)

        expect:
        modelOne
        modelTwo
        parentDataClass
        childDataClass

        parentDataClass.dataModel == modelOne
        childDataClass.dataModel == modelTwo

    }

    def "adds a data model if there is an attempt to add classification or declaration relationship"() {
        build {
            dataModel name: 'DTDMCDR Test'
            dataType name: 'DTDMCDR', {
                rel 'declaration' from 'DTDMCDR Test'
            }
        }

        DataType dataType = DataType.findByName('DTDMCDR')

        expect:
        dataType
        dataType.dataModel
        dataType.dataModel.name == 'DTDMCDR Test'
    }

    def "updating relationship to deprecated item will trigger new draft"() {
        String dm1name = "URD DM1"
        String dc1name = "URD DC1"
        String dc2name = "URD DC2"

        build {
            dataModel name: dm1name,  {
                dataClass name: dc1name, status: deprecated
            }
        }

        expect:
        DataClass.countByName(dc1name) == 1
        DataClass.findByName(dc1name).status == ElementStatus.DEPRECATED

        when:
        build {
            dataModel name: dm1name, {
                dataClass name: dc2name, {
                    rel 'hierarchy' from dc1name
                }
            }
        }

        then:
        noExceptionThrown()
        DataClass.countByName(dc1name) == 2
        // and the original deprecated
        DataClass.findByNameAndStatus(dc1name, ElementStatus.DEPRECATED)
        // there is one draft
        DataClass.findByNameAndStatus(dc1name, ElementStatus.DRAFT)
    }
}
