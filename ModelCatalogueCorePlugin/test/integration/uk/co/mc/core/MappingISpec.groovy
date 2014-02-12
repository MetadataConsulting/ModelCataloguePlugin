package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

class MappingISpec extends Specification {

    @Unroll
    def "create a mew data type from #args validates to #validates" (){

        expect:

        Mapping.list().isEmpty()

        when:

        Mapping type = new Mapping(args)
        type.save()
        println type.errors

        then:

        !type.hasErrors() == validates
        Mapping.list().size() == size

        where:
        validates | size | args
        false     | 0    | [:]
        false     | 0    | [name: "x" * 256]
        false     | 0    | [
                name: "String",
                source: new ValueDomain(
                        unitOfMeasure: new MeasurementUnit(name: "Degrees of C").save(),
                        name: "value domain source",
                        dataType: new DataType(name: "degree").save()
                ).save(),
                destination: new ValueDomain(
                        unitOfMeasure: new MeasurementUnit(name: "Degrees of F").save(),
                        name: "value domain destination",
                        dataType: new DataType(name: "integer").save()
                ).save(),
                mapping: "foo"
        ]
        true      | 1    | [
                name: "String",
                source: new ValueDomain(
                        unitOfMeasure: new MeasurementUnit(name: "Degrees of C 2").save(),
                        name: "value domain source 2",
                        dataType: new DataType(name: "degree 2").save()
                ).save(),
                destination: new ValueDomain(
                        unitOfMeasure: new MeasurementUnit(name: "Degrees of F 2").save(),
                        name: "value domain destination 2",
                        dataType: new DataType(name: "integer 2").save()
                ).save(),
                mapping: "x * 2"
        ]

    }

    def "create mapping using map "(){
        MeasurementUnit mu1 = new MeasurementUnit(name: "Degrees of C").save()
        MeasurementUnit mu2 = new MeasurementUnit(name: "Degrees of F").save()

        DataType floatType  = new DataType(name: "float").save()

        ValueDomain vd1     = new ValueDomain(name: "Patient`s Temperature in C", dataType: floatType, unitOfMeasure: mu1).save()
        ValueDomain vd2     = new ValueDomain(name: "Patient`s Temperature in F", dataType: floatType, unitOfMeasure: mu2).save()

        when:
        Mapping zero = Mapping.map(vd1, new ValueDomain(name: "none"), "x")

        then:
        !zero

        when:
        Mapping first = Mapping.map(vd1, vd2, "x * 9/5 + 32")

        then:
        first
        !first.hasErrors()
        vd1.outgoingMappings
        vd1.outgoingMappings.contains(first)
        vd2.incomingMappings
        vd2.incomingMappings.contains(first)

        when:
        Mapping second = Mapping.map(vd1, vd2, "x * 9/5 + 32")

        then:
        second
        !second.hasErrors()
        first.id == second.id
        vd1.outgoingMappings
        vd1.outgoingMappings.contains(first)
        vd2.incomingMappings
        vd2.incomingMappings.contains(first)

        when:
        Mapping third = Mapping.unmap(vd1, vd2)

        then:
        third
        third.id == first.id
        !vd1.outgoingMappings
        !vd1.outgoingMappings.contains(first)
        !vd2.incomingMappings
        !vd2.incomingMappings.contains(first)

        when:
        Mapping fourth = Mapping.unmap(vd1, vd2)

        then:
        !fourth
        !vd1.outgoingMappings
        !vd1.outgoingMappings.contains(first)
        !vd2.incomingMappings
        !vd2.incomingMappings.contains(first)
    }
}
