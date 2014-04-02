package org.modelcatalogue.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 03/02/2014.
 */
@Mock (DataType)
class DataTypeSpec extends Specification{


    @Unroll
    def "create a new data type from #args validates to #validates" (){

        expect:

        DataType.list().isEmpty()

        when:

        DataType type = new DataType(args)
        type.save()


        then:

        !type.hasErrors() == validates
        DataType.list().size() == size

        where:
        validates | args             | size
        false     | [:]              | 0
        false     | [name: "x" *256] | 0
        true      | [name: "String"] | 1

    }


    def "check  EqualsAndHashCode works"(){

        when:
        def a = new DataType(name:"test", description: "test concept description")
        def b = new DataType(name:"test", description: "test concept description")
        def c = new DataType(name:"test conceptasdsfdfsad", description: "test concept description")

        then:
        a.equals(b)
        b.equals(a)
        !a.equals(c)

    }

}
