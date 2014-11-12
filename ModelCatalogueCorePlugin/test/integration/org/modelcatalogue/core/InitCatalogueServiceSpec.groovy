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
        RelationshipType dt2 = RelationshipType.findByName("classification")
        RelationshipType dt3 = RelationshipType.findByName("supersession")
        RelationshipType dt4 = RelationshipType.findByName("hierarchy")

        then:
        dt1
        dt2
        dt3
        dt4
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

    def "The supersession is present withing default relations types"(){
        when:
        initCatalogueService.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.supersessionType

        then:
        loaded
        loaded.sourceClass == CatalogueElement
        loaded.destinationClass == CatalogueElement
        loaded.sourceToDestination == "superseded by"
        loaded.destinationToSource == "supersedes"
        loaded.name == "supersession"

        !loaded.validateRule(new Model(), new DataElement(), [:])
        loaded.validateRule(new DataElement(), new DataElement(), [:])

    }

}
