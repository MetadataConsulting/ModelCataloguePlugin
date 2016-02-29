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

        iterator.hasNext()

        when:
        Enumeration second = iterator.next()

        then:
        second
        second.id == 2L
        second.key == '02'
        second.value == 'two'

        iterator.hasNext()

        when:
        Enumeration third = iterator.next()

        then:
        third.id == 3L
        third.key == '03'
        third.value == 'three'


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
