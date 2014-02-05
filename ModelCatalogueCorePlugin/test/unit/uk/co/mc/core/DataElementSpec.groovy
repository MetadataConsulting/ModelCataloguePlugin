package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 */
@Mock(DataElement)
class DataElementSpec extends Specification{

    @Unroll
    def "create a new data element from #args validates to #validates"(){

        expect:

        DataElement.list().isEmpty()

        when:

        DataElement dataElementInstance = new DataElement(args)

        dataElementInstance.save()

        then:

        !dataElementInstance.hasErrors()==validates
        DataElement.list().size() == size

        where:

        validates |   size    | args
        false     |   0       | [name: "x" * 256, description: "this is the the result description", definition: "this is the result definition"]
        false     |   0       | [name: "x", description: "x"*2001, definition: "this is the result definition"]
        false     |   0       | [name: "result1", description: "this is the the result description", definition: "x" * 2001]
        true      |   1       | [name: "result1", description: "this is the the result description", definition: "this is the result definition"]

    }

}
