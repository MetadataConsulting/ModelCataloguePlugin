package org.modelcatalogue.core.util

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataElement
import spock.lang.Unroll

class DetachedListWrapperSpec extends IntegrationSpec {

    @Unroll
    def "list wrapper backed by detached criteria, size is #size for max #max"() {
        new DataElement(name: 'test 1').save(failOnError: true)
        new DataElement(name: 'test 2').save(failOnError: true)
        new DataElement(name: 'tset 0').save(failOnError: true)

        ListWrapper<DataElement> wrapper = Lists.fromCriteria(DataElement, '/dataElements/', max: max, sort: 'name', order: 'asc') {
            ilike 'name', 'test%'
        }

        expect:
        wrapper.base         == '/dataElements/'
        wrapper.items.size() == size
        wrapper.total        == 2
        wrapper.offset       == 0
        wrapper.page         == max
        wrapper.sort         == 'name'
        wrapper.order        == 'asc'
        wrapper.itemType     == DataElement
        wrapper.next         != null
        wrapper.previous     != null

        when:
        wrapper.total        = 10

        then:
        thrown(ReadOnlyPropertyException)

        when:
        wrapper.items        = []

        then:
        thrown(ReadOnlyPropertyException)

        where:

        max | size
        1   | 1
        2   | 2
        3   | 2

    }

}
