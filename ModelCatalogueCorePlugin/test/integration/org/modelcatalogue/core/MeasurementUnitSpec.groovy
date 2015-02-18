package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll
/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 * a measurement unit can be
 */
class MeasurementUnitSpec extends IntegrationSpec {

    @Unroll
    def "create a new measurement from #args validates to #validates"() {
        int initialSize = MeasurementUnit.count()
        when:

        MeasurementUnit measurementUnitInstance = new MeasurementUnit(args)

        measurementUnitInstance.save()

        then:

        !measurementUnitInstance.hasErrors() == validates
        MeasurementUnit.count() == size + initialSize


        where:

        validates | size | args
        false     | 0    | [name: "x" * 256, description: "this is the the result description"]
        false     | 0    | [name: "x", description: "x" * 2001]
        true      | 1    | [name: "MPH", description: "Miles per Hour",]
        true      | 1    | [name: "Miles per Hour", description: "Number of miles the object achieves within one hour moving the current speed.", symbol: "MPH"]
        false     | 0    | [name: "Miles per Hour", description: "Number of miles the object achieves within one hour moving the current speed.", symbol: "x" * 101]

    }

}
