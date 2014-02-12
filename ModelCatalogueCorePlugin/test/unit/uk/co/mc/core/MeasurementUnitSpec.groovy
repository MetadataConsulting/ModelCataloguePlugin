package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 * a measurement unit can be
 */
@Mock(MeasurementUnit)
class MeasurementUnitSpec extends Specification {

    @Unroll
    def "create a new measurement from #args validates to #validates"(){

        expect:

        MeasurementUnit.list().isEmpty()

        when:

        MeasurementUnit measurementUnitInstance = new MeasurementUnit(args)

        measurementUnitInstance.save()

        then:

        !measurementUnitInstance.hasErrors()==validates
        MeasurementUnit.list().size() == size


        where:

        validates |   size    | args
        false     |   0       | [name: "x" * 256, description: "this is the the result description"]
        false     |   0       | [name: "x", description: "x"*2001]
        true      |   1       | [name: "MPH", description: "Miles per Hour",]

    }
}
