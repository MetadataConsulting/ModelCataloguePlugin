package org.modelcatalogue.core.scripting

import spock.lang.Specification

/**
 * @author James Dai
 * Spec the behaviour of our validation rule DSL
 */
class DataTypeRuleScriptSpec extends Specification {



    def "test 'is' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "is(number) && x == 3" | 3 // test
        "is(string)" | 'abc' // test
        "is(string)" | 123 // test conversion
        "is(integer)" | 123.123
        "is(number) && x == 3 && x != '3'" | '3' // is converts x to a number so it is not equal to the string anymore
        "is(decimal) && x == 3.3" | '3.3' //conversion
        "is(integer) && x == 3" | '3' // conversion
        "is(string) && x == '3'" | 3 // converts number to string

    }

    def "test 'canConvertTo' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule                   | x
        "canConvertTo(number) && x == 3" | 3 // test
        "canConvertTo(string)"           | 'abc' // test
        "canConvertTo(string)"           | 123 // test whether can convert
        "canConvertTo(integer)"          | 123.123

        "!(canConvertTo(number) && x == 3 && x != '3')" | '3' // canConvertTo does not actually convert x to a number so it canConvertTo not equal to the string anymore
        "!(canConvertTo(decimal) && x == 3.3)" | '3.3' // no conversion
        "!(canConvertTo(integer) && x == 3)" | '3' // no conversion
        "!(canConvertTo(string) && x == '3')" | 3 // no conversion
    }
    def "test 'string' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "string(x) == ''" | null
        "string(x) == 'abc'" | 'abc'
        "string(x) == '3'" | 3
    }

    def "test 'number' method"() {
        // TODO
    }


    def "test 'fixed' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "fixed(004)" | 04

    }


    def "test 'minLength' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "minLength(1)" | "a"
        "minLength(1)" | 0
        "!minLength(1)" | ""

    }

    def "test 'maxLength' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "maxLength(1)" | 'a'
        "!maxLength(1)" | 'aa'
    }

    def "test 'minInclusive' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x

        "!minInclusive(3)" | 2
        "minInclusive(3)" | 3
        "minInclusive(3)" | 4
    }
    def "test 'maxInclusive' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "maxInclusive(3)" | 2
        "maxInclusive(3)" | 3
        "!maxInclusive(3)" | 4
    }
    def "test 'minExclusive' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "!minExclusive(3)" | 2
        "!minExclusive(3)" | 3
        "minExclusive(3)" | 4
    }
    def "test 'maxExclusive' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "maxExclusive(3)" | 2
        "!maxExclusive(3)" | 3
        "!maxExclusive(3)" | 4
    }

    def "test 'length' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "!length(1)" | ''
        "length(1)" | 'a'
        "!length(1)" | 'aa'
    }

    def "test 'totalDigits' method"() {

        when:
        ValueValidator.evaluateRule("totalDigits(0)" | 0) // totalDigits does not accept 0
        then:
        thrown(Exception)

        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        'totalDigits(3)' | 999
        'totalDigits(3)' | 0.999
        'totalDigits(3)' | 9.99

        '!totalDigits(3)' | 1000
        '!totalDigits(3)' | 0.0999
        '!totalDigits(3)' | 99.99

    }

    def "test 'fractionDigits' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        'fractionDigits(3)' | 99999999.999
        '!fractionDigits(3)' | 0.9999
        'fractionDigits(0)' | 1
        'fractionDigits(0)' | 01.0
    }

    def "test regular expression"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "x ==~ /\\d+(\\.\\d+)?/" | 3.3
    }

    def "test values inset"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        "x in ['apple', 'banana', 'cherry']" | 'apple'
        "!(x in ['apple', 'banana', 'cherry'])" | 'appl'
    }

    def "test 'allTrue' method"() {
        expect:
        ValueValidator.evaluateRule(rule, x)

        where:
        rule | x
        //"allTrue[minLength(1), maxLength(1)]" | 'a'
        "minLength(1)\n && maxLength(1)" | 'a'
    }

    def "test date"() {
        // TODO
    }

    def "test getX and setX"() {
        // TODO
    }
}
