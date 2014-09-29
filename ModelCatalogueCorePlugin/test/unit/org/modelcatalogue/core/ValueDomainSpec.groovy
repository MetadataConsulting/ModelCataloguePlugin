package org.modelcatalogue.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

/**
 * Created by adammilward on 05/02/2014.
 * A value domain instantiates a data element it includes information regarding the set of values and constraints that
 * describe the data element within the context of a conceptual domain
 * value domains are invluded in conceptual domains
 */
@Mock([ValueDomain, DataElement, MeasurementUnit, DataType, EnumeratedType])
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
        true  | [name: "e", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: null]
        true  | [name: "ground_speed1", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: null]
        false | [name: "ground_speed2", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "x" * 2001, dataType: new DataType(name: "Float")]
        false | [name: "ground_speed3", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "x" * 10001, description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        false | [name: "ground_speed4", unitOfMeasure: "x" * 256, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        false | [name: "x" * 256, unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        true  | [name: "ground_speed6", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        true  | [name: "ground_speed7", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: ['m': 'male', 'f': 'female', 'u': 'unknown'])]

    }

    def "check one to many relationship exits between data type and value domain"(){

        when:

        def mu = new MeasurementUnit(name: "MPH").save()
        def dt = new DataType(name: "Float").save()
        def valueDomain = new ValueDomain(name: "ground_speed6", unitOfMeasure: mu, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: dt).save()

        then:

        !valueDomain.hasErrors()
        dt.relatedValueDomains.contains(valueDomain)

    }

    def "check one to many relationship exits between enumerated type= and value domain"(){

        when:

        def mu = new MeasurementUnit(name: "MPH").save()
        def et = new EnumeratedType(name: 'test', enumerations: ['m': 'male', 'f': 'female', 'u': 'unknown']).save()
        def valueDomain = new ValueDomain(name: "ground_speed6", unitOfMeasure: mu, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: et).save()

        then:

        !valueDomain.hasErrors()
        et.relatedValueDomains.contains(valueDomain)

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
    }


    def "check toString works"() {

        when:
        def a = new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name: "MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: [male: 'male', female: 'female', unknown:'unknown'])).save()

        then:
        a.toString() == "ValueDomain[id: 1, name: ground_speed]"

    }

    def "can covert regexp to rule"() {
        ValueDomain domain = new ValueDomain(regexDef: regexp)

        expect:
        domain.rule == rule
        domain.regexDef == regexp
        domain.validateRule(matches)


        where:
        regexp         | rule                   | matches
        "(?i)google"   | "x ==~ /(?i)google/"  | "gOOgle"

    }

    def "validates rule"() {
        expect:
        new ValueDomain(rule: rule).validateRule(x)

        where:
        rule        | x
        "x > 10"    | 20
        "x ==~ /(?i)google/ || x ==~/g01gle/" |  "g01gle"
    }


    def "uses enum type enum constants for validation"() {
        ValueDomain domain = new ValueDomain(dataType: new EnumeratedType(enumerations: [one: '1', two: '2']), regexDef: /[a-z]{3}/)

        expect:
        domain.validateRule(value) == result


        where:
        result  | value
        true    | 'one'
        true    | 'two'
        false   | 'six'
        false   | 'blah'

    }

    @ConfineMetaClassChanges(ValueDomain)
    def "uses base for validation"() {
        ValueDomain domain = new ValueDomain(dataType: new EnumeratedType(enumerations: [one: '1', two: '2']), regexDef: /[a-z]{3}/)
        ValueDomain other  = new ValueDomain()

        other.metaClass.basedOn = [domain]

        expect:
        domain.validateRule(value) == result


        where:
        result  | value
        true    | 'one'
        true    | 'two'
        false   | 'six'
        false   | 'blah'

    }

}
