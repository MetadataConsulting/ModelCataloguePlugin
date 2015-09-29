package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges
/**
 * Created by adammilward on 05/02/2014.
 * A value domain instantiates a data element it includes information regarding the set of values and constraints that
 * describe the data element within the context of a conceptual domain
 * value domains are invluded in conceptual domains
 */
class ValueDomainSpec extends IntegrationSpec {

    def fixtureLoader

    @Unroll
    def "Value Domain creation for #args results in #validates"() {
        when:
        ValueDomain valueDomainInstance = new ValueDomain(args)
        valueDomainInstance.save()

        then:
        !valueDomainInstance.hasErrors() == validates

        where:
        validates | args
        true  | [name: "e", unitOfMeasure: createMeasurementUnit(), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: null]
        true  | [name: "ground_speed1", unitOfMeasure: createMeasurementUnit(), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: null]
        false | [name: "ground_speed2", unitOfMeasure: createMeasurementUnit(), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "x" * 2001, dataType: createDataType()]
        false | [name: "ground_speed3", unitOfMeasure: createMeasurementUnit(), regexDef: "x" * 10001, description: "the ground speed of the moving vehicle", dataType: createDataType()]
        false | [name: "ground_speed4", unitOfMeasure: "x" * 256, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: createDataType()]
        false | [name: "x" * 256, unitOfMeasure: createMeasurementUnit(), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: createDataType()]
        true  | [name: "ground_speed6", unitOfMeasure: createMeasurementUnit(), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: createDataType()]
        true  | [name: "ground_speed7", unitOfMeasure: createMeasurementUnit(), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: createEnumeratedType()]

    }

    private static EnumeratedType createEnumeratedType() {
        new EnumeratedType(name: 'test', enumerations: ['m': 'male', 'f': 'female', 'u': 'unknown']).save(failOnError: true)
    }

    private static MeasurementUnit createMeasurementUnit() {
        new MeasurementUnit(name: "MPH${System.currentTimeMillis()}").save(failOnError: true)
    }

    private static DataType createDataType() {
        new DataType(name: "Float").save(failOnError: true)
    }

    def "check one to many relationship exits between data type and value domain"(){

        when:

        def mu = createMeasurementUnit()
        def dt = createDataType()
        def valueDomain = new ValueDomain(name: "ground_speed6", unitOfMeasure: mu, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: dt).save()

        then:

        !valueDomain.hasErrors()
        dt.relatedValueDomains.contains(valueDomain)

    }

    def "check one to many relationship exits between enumerated type= and value domain"(){

        when:

        def mu = createMeasurementUnit()
        def et = new EnumeratedType(name: 'test', enumerations: ['m': 'male', 'f': 'female', 'u': 'unknown']).save()
        def valueDomain = new ValueDomain(name: "ground_speed6", unitOfMeasure: mu, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: et).save()

        then:

        !valueDomain.hasErrors()
        et.relatedValueDomains.contains(valueDomain)

    }


    def "create value domain with invalid regex definition"() {
        when:
        ValueDomain valueDomainInstance = [regexDef: "(blah"]
        valueDomainInstance.validate()

        then:
        valueDomainInstance.hasErrors()
    }


    def "check toString works"() {

        when:
        def a = new ValueDomain(name: "ground_speed", unitOfMeasure: createMeasurementUnit(), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name: 'test', enumerations: [male: 'male', female: 'female', unknown:'unknown']).save(failOnError: true)).save()

        then:
        a.toString() == "ValueDomain[id: ${a.id}, name: ground_speed, status: DRAFT, modelCatalogueId: null]"

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

        other.metaClass.isBasedOn = [domain]

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
