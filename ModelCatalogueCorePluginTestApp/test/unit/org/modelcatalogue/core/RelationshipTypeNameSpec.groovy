package org.modelcatalogue.core

import spock.lang.Specification

class RelationshipTypeNameSpec extends Specification {

    def "name is lowercase"() {
        expect:
        RelationshipTypeName.IMPORT.name == 'import'

        and:
        RelationshipTypeName.IMPORT.toString() == 'import'
    }
}
