package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 */
@Mock([ValueDomain, DataElement])
class ValueDomainSpec extends Specification{

    @Unroll
    def "Value Domain creation for #args results in #validates"()
    {

        expect:

        ValueDomain.list().isEmpty()

        when:

        ValueDomain valueDomainInstance = new ValueDomain(args)

        valueDomainInstance.save()

        then:

        !valueDomainInstance.hasErrors() == validates

        where:

        validates | args
        false     | [name: "e", unitOfMeasure: "mph", regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", format: "floating point", description: "the ground speed of the moving vehicle", dataType: null]
        false     | [name: "ground_speed", unitOfMeasure: "mph", regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", format: "floating point", description: "the ground speed of the moving vehicle", dataType: null]
        false     | [name: "ground_speed", unitOfMeasure: "mph", regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", format: "floating point", description: "x" * 2001, dataType: new DataType(name: "Float")]
        false     | [name: "ground_speed", unitOfMeasure: "mph", regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", format: "x" * 256, description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        false     | [name: "ground_speed", unitOfMeasure: "mph", regexDef: "x" * 501, format: "floating point", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        false     | [name: "ground_speed", unitOfMeasure: "x" * 256, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", format: "floating point", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        false     | [name: "x" * 256, unitOfMeasure: "mph", regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", format: "floating point", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        true      | [name: "ground_speed", unitOfMeasure: "mph", regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", format: "floating point", description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")]
        true      | [name: "ground_speed", unitOfMeasure: "mph", regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", format: "floating point", description: "the ground speed of the moving vehicle", dataType: new EnumeratedType(name:'test', enumerations: ['male','female','unknown'])]

    }

}
