package uk.co.mc.core

import grails.test.spock.IntegrationSpec
import spock.lang.Shared

/**
 * Created by adammilward on 05/02/2014.
 */

class DeleteThingsSpec extends IntegrationSpec{

    @Shared
    def fixtureLoader


    def "check delete works"(){

        def m, et, vd1
        expect:
        assert(m = new MeasurementUnit(name:"cm per hour", symbol: "cmph").save())
        assert(et = new EnumeratedType(name: "enum", enumerations:['1':'this', '2':'that', '3':'theOther']).save())
        assert(vd1 = new ValueDomain(name: "ground_speed", unitOfMeasure: m, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: et).save())

        when:

        m.delete(flush:true, failOnError:true)
        et.delete(flush:true, failOnError:true)


        then:

        !m.id

        //!m.id
        //!et.id

        println(vd1.unitOfMeasure)
        //vd1.unitOfMeasure.id
        //vd1.dataType.id



    }



}
