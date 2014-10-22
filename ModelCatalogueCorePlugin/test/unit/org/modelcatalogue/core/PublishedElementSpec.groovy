package org.modelcatalogue.core

import spock.lang.Specification

class PublishedElementSpec extends Specification {


    def "Name with classifications"() {
        PublishedElement model = new Model(name: "Test")

        expect:
        model.classifiedName == "Test"

        when:
        model.classifications << new Classification(name: "BLAH")

        then:
        model.classifiedName == "Test (BLAH)"

        when:
        model.classifications << new Classification(name: "ABC")

        then:
        model.classifiedName == "Test (ABC, BLAH)"

    }

}
