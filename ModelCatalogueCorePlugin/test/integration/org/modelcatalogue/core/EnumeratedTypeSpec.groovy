package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll
/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class EnumeratedTypeSpec extends IntegrationSpec {


    @Unroll
    def "validates to #validates for #args.name "() {
        when:
        EnumeratedType etype = new EnumeratedType(args)

        etype.save()

        then:

        !etype.hasErrors() == validates

        where:

        validates | args
        false     | [:]
        false | [name: 'test1']
        false | [name: 'test2', enumerations: ['male']]
        false | [name: 'test3', enumAsString: ('m:s|' * 2500) + 's:m']
        true  | [name: 'test4', enumerations: ['m': 'male', 'f': 'female', 'u': 'unknown']]
    }

    @Unroll
    def "Serialize enumerations #enumerations to normalized string #expected"() {
        expect:
        EnumeratedType.mapToString(enumerations) == expected
        EnumeratedType.stringToMap(expected) == enumerations

        where:
        enumerations                   | expected
        null                           | null
        [one: 'one', two: "two"]       | "one:one|two:two"
        [one: 'o:ne', two: "tw|o"]     | "one:o&#58;ne|two:tw&#124;o"
        [one: 'o\\:ne', two: "tw\\|o"] | "one:o&#92;&#58;ne|two:tw&#92;&#124;o"
        [one: 'o\\ne', two: "tw\\o"]   | "one:o&#92;ne|two:tw&#92;o"
        [one: 'one ', two: " two"]     | "one:one |two: two"
    }

    @Unroll
    def "String '#original' is quoted as '#expected'"() {
        expect:
        EnumeratedType.quote(original) == expected
        EnumeratedType.unquote(expected) == original

        where:
        original | expected
        null     | null
        'one'    | "one"
        'o:ne'   | "o&#58;ne"
        'o\\:ne' | "o&#92;&#58;ne"
        'o\\ne'  | "o&#92;ne"
        'one '   | "one "
    }


    def "Find enumerated types by key or value"() {
        expect:
        new EnumeratedType(name: "numbers", enumerations: [one: 'one', two: 'two', three: 'three']).save()
        new EnumeratedType(name: "times", enumerations: [one: 'single', two: 'coupe', three: 'triple']).save()
        new EnumeratedType(name: "something", enumerations: [foo: 'one function', bar: 'two twins ', barbar: 'three little piglets']).save()

        when:
        List<EnumeratedType> ones = EnumeratedType.findAllByEnumeratedKey('one')

        then:
        ones
        ones.size() == 2
        ones.any { it.name == 'numbers' }
        ones.any { it.name == 'times' }

        when:
        List<EnumeratedType> twos = EnumeratedType.findAllByEnumeratedValue('two')

        then:
        twos
        twos.size() == 1
        twos.first().name == 'numbers'

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
