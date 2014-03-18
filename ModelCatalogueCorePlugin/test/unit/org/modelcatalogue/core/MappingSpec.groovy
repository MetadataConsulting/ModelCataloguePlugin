package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Mapping)
class MappingSpec extends Specification {

    void "map to function"() {
        def map = [1: "one", 2: "two", 3: "three"]

        when:
        String mapFunctionString = MappingService.createMappingFunctionFromMap(map)

        then:
        mapFunctionString == """[1:"one", 2:"two", 3:"three"][x]"""
        new GroovyShell(new Binding(x: 2)).evaluate(mapFunctionString) == "two"
    }


    void "map value using static method"() {
        expect:
        Mapping.mapValue("2*x", 2) == 4
    }

    void "map value using instance method"() {
        expect:
        new Mapping(mapping: "5*x").map(4) == 20
    }


}
