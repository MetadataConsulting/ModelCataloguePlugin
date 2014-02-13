package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * Created by ladin on 10.02.14.
 */
@Mock(RelationshipType)
class RelationshipTypeSpec extends Specification {

    def "you can init default types without duplicates"() {
        expect:
        RelationshipType.count() == 0

        when: "the init method is run for the first type"
        RelationshipType.initDefaultRelationshipTypes()
        int defaultTypesCount = RelationshipType.count()

        then: "there are some default types"
        defaultTypesCount

        when: "the init method again"
        RelationshipType.initDefaultRelationshipTypes()

        then: "no types are added again"
        defaultTypesCount == RelationshipType.count()
    }

    def "The containment is present withing default relations types"(){
        when:
        RelationshipType.initDefaultRelationshipTypes()
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
        RelationshipType.initDefaultRelationshipTypes()
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
        RelationshipType.initDefaultRelationshipTypes()
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
        RelationshipType.initDefaultRelationshipTypes()
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
        RelationshipType.initDefaultRelationshipTypes()
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
        RelationshipType.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.supersessionType

        then:
        loaded
        loaded.sourceClass == PublishedElement
        loaded.destinationClass == PublishedElement
        loaded.sourceToDestination == "superseded by"
        loaded.destinationToSource == "supersedes"
        loaded.name == "supersession"

        !loaded.validateRule(new Model(), new DataElement())
        loaded.validateRule(new DataElement(), new DataElement())

    }

    def "Validate rule"() {
        RelationshipType type = new RelationshipType(rule: rule)

        expect:
        type.validateRule(new DataElement(), new DataElement()) == expected


        where:
        expected | rule
        true     | "true"
        false    | "false"
        true     | "source.class == destination.class"
    }


    def "Get link info"() {
        RelationshipType.initDefaultRelationshipTypes()
        RelationshipType loaded = RelationshipType.containmentType

        expect:
        loaded

        loaded.info
        loaded.info.name == loaded.name
        loaded.info.id == loaded.id
        loaded.info.link == "/relationshipType/${loaded.id}"
    }
}
