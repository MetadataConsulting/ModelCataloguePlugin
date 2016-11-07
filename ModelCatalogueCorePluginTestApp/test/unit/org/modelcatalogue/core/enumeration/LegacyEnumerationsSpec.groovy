package org.modelcatalogue.core.enumeration

import org.modelcatalogue.core.EnumeratedType
import spock.lang.Specification
import spock.lang.Unroll

class LegacyEnumerationsSpec extends Specification {


    @Unroll
    def "Serialize enumerations #enumerations to normalized string #expected"() {
        expect:
        LegacyEnumerations.mapToString(enumerations) == expected
        LegacyEnumerations.stringToMap(expected) == enumerations

        where:
        enumerations                   | expected
        [one: 'one', two: "two"]       | "one:one|two:two"
        [one: 'o:ne', two: "tw|o"]     | "one:o&#58;ne|two:tw&#124;o"
        [one: 'o\\:ne', two: "tw\\|o"] | "one:o&#92;&#58;ne|two:tw&#92;&#124;o"
        [one: 'o\\ne', two: "tw\\o"]   | "one:o&#92;ne|two:tw&#92;o"
        [one: 'one ', two: " two"]     | "one:one |two: two"
    }

    def "null string produces empty map"() {
        expect:
        LegacyEnumerations.stringToMap(null) == [:]
    }

    def "null map produces empty string"() {
        expect:
        LegacyEnumerations.mapToString(null) == ""
    }

    @Unroll
    def "String '#original' is quoted as '#expected'"() {
        expect:
        LegacyEnumerations.quote(original) == expected
        LegacyEnumerations.unquote(expected) == original

        where:
        original | expected
        'one'    | "one"
        'o:ne'   | "o&#58;ne"
        'o\\:ne' | "o&#92;&#58;ne"
        'o\\ne'  | "o&#92;ne"
        'one '   | "one "
    }


    def "null is (un)quoted as empty string"() {
        expect:
        LegacyEnumerations.quote(null) == ""
        LegacyEnumerations.unquote(null) == ""
    }


    def "pretty print enumeration"() {
        EnumeratedType type = new EnumeratedType(name: 'Test123', enumerations: [one: '001', three: '003', two: '002'])

        expect:
        type.prettyPrint() == '''
            one: 001
            three: 003
            two: 002
        '''.stripIndent().trim()
    }
}
