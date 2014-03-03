package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 * A value domain instantiates a data element it includes information regarding the set of values and constraints that
 * describe the data element within the context of a conceptual domain
 * value domains are invluded in conceptual domains
 */
@Mock([ValueDomain, DataElement])
class ValueDomainSpec extends Specification {

    def fixtureLoader

    @Unroll
    def "Value Domain creation for #args results in #validates"() {

        expect:

        ValueDomain.list().isEmpty()

        when:

        ValueDomain valueDomainInstance = new ValueDomain(args)

        valueDomainInstance.save()

        then:

        !valueDomainInstance.hasErrors() == validates

        where:

        validates | args
        false | [name: "e", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: null]
        false | [name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: null]
        false | [name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "x" * 2001, dataType: new DataType(name: "Float")]
        false | [name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "x" * 501, description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        false | [name: "ground_speed", unitOfMeasure: "x" * 256, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        false | [name: "x" * 256, unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        false | [name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+?", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        true  | [name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        true  | [name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: ['male', 'female', 'unknown'])]

    }


    def "create value domain with invalid regex definition"() {

        expect:

        ValueDomain.list().isEmpty()

        when:

        ValueDomain valueDomainInstance = [regexDef: "(blah"]
        valueDomainInstance.validate()

        then:

        valueDomainInstance.hasErrors()
        // the third argument is the real error message passed from the validator
        valueDomainInstance.errors.getFieldError("regexDef").arguments[3] == "Unclosed group near index 5\n(blah\n     ^"

    }


    def "check toString works"() {

        when:
        def a = new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: ['male', 'female', 'unknown'])).save()

        then:
        a.toString() == "ValueDomain[id: 1, name: ground_speed]"

    }

//    def "check  EqualsAndHashCode works"() {
//
//        when:
//        def a = new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: ['male', 'female', 'unknown'])).save()
//        def b = new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: ['male', 'female', 'unknown'])).save()
//        def c = new ValueDomain(name: "ground_speed1", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: ['male', 'female', 'unknown'])).save()
//        def d = new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test1', enumerations: ['male', 'female', 'unknown'])).save()
//        def e = new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "KPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: ['male', 'female', 'unknown'])).save()
//        def f = new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "string")).save()
//
//
//        then:
//        a.equals(b)
//        b.equals(a)
//        !a.equals(c)
//        !a.equals(d)
//        !a.equals(e)
//        !a.equals(f)
//
//    }

}
