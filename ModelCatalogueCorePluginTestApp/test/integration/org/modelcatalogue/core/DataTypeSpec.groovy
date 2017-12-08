package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll
/**
 * Created by adammilward on 03/02/2014.
 */
class DataTypeSpec extends IntegrationSpec {

    @Unroll
    def "create a new data type from #args validates to #validates"() {
        int initialSize = DataType.count()

        when:
        DataType type = new DataType(args)
        type.save()

        then:
        !type.hasErrors() == validates
        DataType.list().size() == size + initialSize

        where:
        validates | args             | size
        false     | [:]              | 0
        false     | [name: "x" *256] | 0
        true      | [name: "String"] | 1
    }

    @Unroll
    def "pick the suggestion #expected from #suggestions"() {
        expect:
        DataType.suggestName(suggestions as Set) == expected

        where:
        expected        | suggestions
        null            | null
        null            | []
        "bla"           | ["bla"]
        "Allred Score"  | ["EstrogenAllredScore", "ProgesteroneAllredScore"]
    }
}
