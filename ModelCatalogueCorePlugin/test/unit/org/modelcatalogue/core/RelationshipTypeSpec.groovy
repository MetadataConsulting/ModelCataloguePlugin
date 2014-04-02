package org.modelcatalogue.core

import grails.test.mixin.Mock
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by ladin on 10.02.14.
 */
@Mock([RelationshipType])
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
