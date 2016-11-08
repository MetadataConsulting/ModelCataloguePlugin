package org.modelcatalogue.core

import grails.test.mixin.Mock
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
        type.validateRule(new DataElement(), new DataElement(), [test: "true"]) == expected


        where:
        expected | rule
        true     | "true"
        false    | "false"
        true     | "source.class == destination.class"
        true     | "ext.test as Boolean"
    }


    def "To camel case"() {
        expect:
        RelationshipType.toCamelCase(words) == result

        where:
        words               | result
        'has attachments'   | 'hasAttachments'
    }

}
