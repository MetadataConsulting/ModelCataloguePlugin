package org.modelcatalogue.core.enumeration

import groovy.json.JsonBuilder
import spock.lang.Specification
import spock.lang.Unroll

class EnumerationsSpec extends Specification {


    @Unroll
    def "you can parse enumeration from #source"() {
        Enumerations enumerations = Enumerations.from(source)

        expect:
        enumerations.size() == 3
        enumerations['01'] == 'one'
        enumerations['02'] == 'two'
        enumerations['03'] == 'three'

        when:
        Iterator<Enumeration> iterator = enumerations.iterator()

        then:
        iterator.hasNext()

        when:
        Enumeration first = iterator.next()

        then:
        first
        first.id == 1L
        first.key == '01'
        first.value == 'one'

        enumerations.getEnumerationById(1L) == first

        iterator.hasNext()

        when:
        Enumeration second = iterator.next()

        then:
        second
        second.id == 2L
        second.key == '02'
        second.value == 'two'

        enumerations.getEnumerationById(2L) == second

        iterator.hasNext()

        when:
        Enumeration third = iterator.next()

        then:
        third.id == 3L
        third.key == '03'
        third.value == 'three'

        enumerations.getEnumerationById(3L) == third

        expect:
        !iterator.hasNext()

        when:
        enumerations.removeEnumerationById(2L)

        then:
        enumerations.size() == 2
        enumerations.getEnumerationById(1L)
        enumerations.getEnumerationById(3L)

        where:
        source << [jsonEnumerationsString, enumerationsMap, LegacyEnumerations.mapToString(enumerationsMap), jsonEnumerationsMap]
    }

    def "enumerations are stored as JSON"() {
        expect:
        Enumerations.from(jsonEnumerationsString).toJsonString() == jsonEnumerationsString
    }

    def "return json enumerations map for marshallers"() {
        expect:
        Enumerations.from(jsonEnumerationsString).toJsonMap() == jsonEnumerationsMap
    }

    def "copy enumeration"() {
        Enumerations enumerations = Enumerations.from(one: 'jedna', two: 'dva')
        Enumerations copy = enumerations.copy()

        expect:
        copy
        copy.size() == 2
        copy.get('one') == 'jedna'
        copy.get('two') == 'dva'
    }

    def "gen id follows after last id"() {
        Enumerations enumerations = Enumerations.create()
        enumerations.put(20L, 'foo', 'bar')
        enumerations.put('zoo', 'war') // should assign 21

        expect:
        enumerations.getEnumerationById(21L)
    }

    def "replace by id"() {
        Enumerations enumerations = Enumerations.create()
        enumerations.put(5, "foo", "bar")
        enumerations.put(5, "zoo", "war")

        expect:
        enumerations.size() == 1
        !enumerations.containsKey("foo")
        enumerations.containsKey("zoo")
        enumerations.getEnumerationById(5)
        enumerations.getEnumerationById(5).key == "zoo"

        when: "the existing key is put with different id"
        enumerations.put(6, "zoo", "bar")
        Enumeration by6 = enumerations.getEnumerationById(6)
        Enumeration by5 = enumerations.getEnumerationById(5)

        then: "it's get stored with the new id but the old one is replaced"
        enumerations.get("zoo") == "bar"
        !by5
        by6
        by6.key == 'zoo'
        by6.value == 'bar'

    }


    def "deprecate by id"() {
        Enumerations enumerations = Enumerations.create()
        enumerations.put(5, "foo", "bar")
        enumerations.put(6, "zoo", "war")

        expect:
        enumerations.size() == 2
        enumerations.containsKey("foo")
        enumerations.containsKey("zoo")

        enumerations.asList()[0].key == 'foo'
        enumerations.asList()[1].key == 'zoo'

        when:
        Enumerations withDeprecatedFoo = enumerations.withDeprecatedEnumeration(5, true)
        Enumeration fooDeprecated = withDeprecatedFoo.getEnumerationById(5)

        then:
        fooDeprecated
        fooDeprecated.key == 'foo'
        fooDeprecated.deprecated

        withDeprecatedFoo.size() == 2

        withDeprecatedFoo.asList()[0].key == 'foo'
        withDeprecatedFoo.asList()[1].key == 'zoo'
    }

    private static Map<String, Object> getJsonEnumerationsMap() {
        return [
            type: 'orderedMap',
            values: [
                [id: 1, key: '01', value: 'one'],
                [id: 2, key: '02', value: 'two'],
                [id: 3, key: '03', value: 'three'],
        ]]
    }

    private static String getJsonEnumerationsString() {
        JsonBuilder json = new JsonBuilder()
        json(jsonEnumerationsMap)
        json.toString()
    }

    private static Map<String, String> getEnumerationsMap() {
        [
            '01': 'one',
            '02': 'two',
            '03': 'three'
        ]
    }

}
