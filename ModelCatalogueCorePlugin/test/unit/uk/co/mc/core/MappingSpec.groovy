package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Mapping)
class MappingSpec extends Specification {

    void "map to function"() {
        def map = [1: "one", 2: "two", 3: "three"]

        when:
        String mapFunctionString = Mapping.createMappingFunctionFromMap(map)

        then:
        mapFunctionString == """[1:"one", 2:"two", 3:"three"][x]"""
        new GroovyShell(new Binding(x: 2)).evaluate(mapFunctionString) == "two"
    }


    void "map value using static method"() {
        expect:
        Mapping.mapValue("2*x", 2) == 4
    }

    void "map value using instance method"(){
        expect:
        new Mapping(mapping: "5*x").map(4) == 20
    }

    def "check  EqualsAndHashCode works"(){

        def vd1 = new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name:"MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?",  description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Double"))
        def vd2 = new ValueDomain(name: "air_speed", unitOfMeasure: new MeasurementUnit(name:"KPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?",  description: "the air speed of the moving plane", dataType: new DataType(name: "Double"))
        def vd3 = new ValueDomain(name: "zoom", unitOfMeasure: new MeasurementUnit(name:"zooming"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?",  description: "the air speed of the moving space ship", dataType: new DataType(name: "Double"))

        when:
        def a = new Mapping(source: vd1, destination: vd2, mapping: "5*x")
        def b = new Mapping(source: vd1, destination: vd2, mapping: "5*x")
        def c = new Mapping(source: vd1, destination: vd3, mapping: "5*x")
        def d = new Mapping(source: vd1, destination: vd2, mapping: "10*x")

        then:
        a.equals(b)
        b.equals(a)
        !a.equals(c)
        !a.equals(d)

    }

}
