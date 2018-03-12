package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(DataType)
class DataTypeSpec extends Specification {

    @Unroll
    def "create a new data type from #args validates to #validates"() {
        when:
        DataType type = new DataType(args)

        when:
        then:
        type.validate() == validates

        where:
        validates | args
        false     | [:]
        false     | [name: "x" *256]
        true      | [name: "String"]
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

    @Unroll
    def "if you replace & HTML entity with from #rule you get #expected"(String rule, String expected) {
        given:
        DataType dataType = new DataType()

        expect:
        expected == dataType.replaceAmpersandHtmlEntityWithAmpersandSymbol(rule)

        where:
        rule                    | expected
        '&amp;foo=bar'          | '&foo=bar'
        '&amp;foo=bar&amp;x=y'  | '&foo=bar&x=y'
        null                    | null
        '&foo=bar'              | '&foo=bar'
        'foo=bar'               | 'foo=bar'
    }
}
