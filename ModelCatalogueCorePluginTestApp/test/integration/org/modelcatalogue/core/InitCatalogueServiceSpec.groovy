package org.modelcatalogue.core

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class InitCatalogueServiceSpec extends AbstractIntegrationSpec {

    def setup() {
        initCatalogue()
    }


    def "init default measurement units"() {
        when:
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
        RelationshipType dt1 = RelationshipType.containmentType
        RelationshipType dt3 = RelationshipType.supersessionType
        RelationshipType dt4 = RelationshipType.hierarchyType

        then:
        dt1
        dt3
        dt4
    }

    def "The containment is present withing default relations types"() {
        when:
        RelationshipType loaded = RelationshipType.containmentType

        then:
        loaded
        loaded.sourceClass == DataClass
        loaded.destinationClass == DataElement
        loaded.sourceToDestination == "contains"
        loaded.destinationToSource == "contained in"
        loaded.name == "containment"

    }

    def "The hierarchy is present withing default relations types"() {
        when:
        RelationshipType loaded = RelationshipType.hierarchyType

        then:
        loaded
        loaded.sourceClass == DataClass
        loaded.destinationClass == DataClass
        loaded.sourceToDestination == "parent of"
        loaded.destinationToSource == "child of"
        loaded.name == "hierarchy"

    }

    def "The supersession is present withing default relations types"() {
        when:
        RelationshipType loaded = RelationshipType.supersessionType

        then:
        loaded
        loaded.sourceClass == CatalogueElement
        loaded.destinationClass == CatalogueElement
        loaded.sourceToDestination == "superseded by"
        loaded.destinationToSource == "supersedes"
        loaded.name == "supersession"

        loaded.validateRule(new DataClass(), new DataElement(), [:]) instanceof List
        loaded.validateRule(new DataElement(), new DataElement(), [:])

    }

}
