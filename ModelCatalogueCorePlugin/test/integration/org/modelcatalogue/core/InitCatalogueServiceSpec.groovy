package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class InitCatalogueServiceSpec extends IntegrationSpec {

    def initCatalogueService

    def setup() {
    }

    def cleanup() {
    }


    def "init default measurement units"() {

        when:
        initCatalogueService.initDefaultMeasurementUnits()
        MeasurementUnit dt1 = MeasurementUnit.findByName("celsius")
        MeasurementUnit dt2 = MeasurementUnit.findByName("fahrenheit")
        MeasurementUnit dt3 = MeasurementUnit.findByName("newtons")

        then:
        dt1
        dt2
        dt3

    }


    def "init default relationship types"() {

        when:
        initCatalogueService.initDefaultRelationshipTypes()
        RelationshipType dt1 = RelationshipType.findByName("containment")
        RelationshipType dt2 = RelationshipType.findByName("context")
        RelationshipType dt3 = RelationshipType.findByName("supersession")
        RelationshipType dt4 = RelationshipType.findByName("hierarchy")
        RelationshipType dt5 = RelationshipType.findByName("inclusion")
        RelationshipType dt6 = RelationshipType.findByName("instantiation")

        then:
        dt1
        dt2
        dt3
        dt4
        dt5
        dt6
    }


    def "you can init default types without duplicates"() {

        when: "the init method is run for the first type"
        initCatalogueService.initDefaultRelationshipTypes()
        int defaultTypesCount = RelationshipType.count()

        then: "there are some default types"
        defaultTypesCount

        when: "the init method again"
        initCatalogueService.initDefaultRelationshipTypes()

        then: "no types are added again"
        defaultTypesCount == RelationshipType.count()
    }

    def "The containment is present withing default relations types"(){
        when:
        initCatalogueService.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.containmentType

        then:
        loaded
        loaded.sourceClass == Model
        loaded.destinationClass == DataElement
        loaded.sourceToDestination == "contains"
        loaded.destinationToSource == "contained in"
        loaded.name == "containment"

    }

    def "The context is present withing default relations types"(){
        when:
        initCatalogueService.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.contextType

        then:
        loaded
        loaded.sourceClass == ConceptualDomain
        loaded.destinationClass == Model
        loaded.sourceToDestination == "provides context for"
        loaded.destinationToSource == "has context of"
        loaded.name == "context"

    }

    def "The hierarchy is present withing default relations types"(){
        when:
        initCatalogueService.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.hierarchyType

        then:
        loaded
        loaded.sourceClass == Model
        loaded.destinationClass == Model
        loaded.sourceToDestination == "parent of"
        loaded.destinationToSource == "child of"
        loaded.name == "hierarchy"

    }

    def "The inclusion is present withing default relations types"(){
        when:
        initCatalogueService.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.inclusionType

        then:
        loaded
        loaded.sourceClass == ConceptualDomain
        loaded.destinationClass == ValueDomain
        loaded.sourceToDestination == "includes"
        loaded.destinationToSource == "included in"
        loaded.name == "inclusion"

    }

    def "The instantiation is present withing default relations types"(){
        when:
        initCatalogueService.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.instantiationType

        then:
        loaded
        loaded.sourceClass == DataElement
        loaded.destinationClass == ValueDomain
        loaded.sourceToDestination == "instantiated by"
        loaded.destinationToSource == "instantiates"
        loaded.name == "instantiation"

    }

    def "The supersession is present withing default relations types"(){
        when:
        initCatalogueService.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.supersessionType

        then:
        loaded
        loaded.sourceClass == PublishedElement
        loaded.destinationClass == PublishedElement
        loaded.sourceToDestination == "superseded by"
        loaded.destinationToSource == "supersedes"
        loaded.name == "supersession"

        !loaded.validateRule(new Model(), new DataElement(), [:])
        loaded.validateRule(new DataElement(), new DataElement(), [:])

    }

    def "check initDataTypes works"(){
        initCatalogueService.initDefaultDataTypes()

        expect:
        DataType.findByName("String")
        DataType.findByName("Integer")
        DataType.findByName("Double")
        DataType.findByName("Boolean")
        DataType.findByName("Date")
        DataType.findByName("Time")
        DataType.findByName("Currency")
    }



}
