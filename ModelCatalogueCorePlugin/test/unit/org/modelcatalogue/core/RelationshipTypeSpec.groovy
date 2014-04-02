package org.modelcatalogue.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by ladin on 10.02.14.
 */
@Mock(RelationshipType)
class RelationshipTypeSpec extends Specification {


    @Unroll
    def "'#name' is valid name == #valid"() {
        def type = new RelationshipType(name: name)
        type.validate()

        expect:
        type.errors.hasFieldErrors("name") == !valid

        where:
        valid | name
        true  | "relationship"
        true  | "relationship1"
        true  | "relation-ship"
        false | "relation ship"
        true  | "Relationship"
        true  | "relationShip"
    }

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

    def "check  EqualsAndHashCode works"(){

        when:
        def rt = new RelationshipType(name:'relationship1', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: CatalogueElement,destinationClass: CatalogueElement).save()
        def rt2 = new RelationshipType(name:'relationship1', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: CatalogueElement,destinationClass: CatalogueElement).save()
        def rt3 = new RelationshipType(name:'relationship2', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: CatalogueElement,destinationClass: CatalogueElement).save()

        then:
        rt.equals(rt2)
        rt2.equals(rt)
        !rt.equals(rt3)

    }


}
