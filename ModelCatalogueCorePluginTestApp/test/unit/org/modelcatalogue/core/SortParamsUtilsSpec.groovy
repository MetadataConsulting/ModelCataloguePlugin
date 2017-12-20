package org.modelcatalogue.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SortParamsUtilsSpec extends Specification {

    @Shared
    List<Book> bookList = [new Book(title: 'Pratical Grails 3'), new Book(title: 'Falando de Grails')]

    def "test sort if you supply an empty map"() {
        when:
        List<Book> result = SortParamsUtils.sort(bookList, [:])

        then:
        result
        result.size() == 2
        result[0].title == 'Pratical Grails 3'
        result[1].title == 'Falando de Grails'
    }

    def "test sort if you supply valid sort and order"() {

        when:
        List<Book> result = SortParamsUtils.sort(bookList, [order: 'desc', sort: 'title'])

        then:
        result
        result.size() == 2
        result[0].title == 'Pratical Grails 3'
        result[1].title == 'Falando de Grails'


        when:
        result = SortParamsUtils.sort(bookList, [order: 'asc', sort: 'title'])

        then:
        result
        result.size() == 2
        result[0].title == 'Falando de Grails'
        result[1].title == 'Pratical Grails 3'
    }

    def "test sort if you supply only sort and no order"() {
        when:
        List<Book> result = SortParamsUtils.sort(bookList, [sort: 'title'])

        then:
        result
        result.size() == 2
        result[0].title == 'Falando de Grails'
        result[1].title == 'Pratical Grails 3'
    }

    def "test sort if you supply an invalid property"() {
        when:
        List<Book> result = SortParamsUtils.sort(bookList, [sort: 'titulo'])

        then:
        result
        result.size() == 2
        result[0].title == 'Pratical Grails 3'
        result[1].title == 'Falando de Grails'
    }

    @Unroll
    def "#order is #description order"(String order, boolean expected, String description) {
        expect:
        expected == SortParamsUtils.validateOrder(order)

        where:
        order       | expected
        'asc'       | true
        'desc'      | true
        'ascendente'| false
        description = expected ? 'valid' : 'not valid'
    }

    @Unroll
    def "order from #params is #order"(Map<String, Object> params, String order) {
        expect:
        order == SortParamsUtils.orderFromParams(params)

        where:
        params                 | order
        [order: 'desc']        | 'desc'
        [order: 'asc']         | 'asc'
        [order: 'ascendente']  | 'asc'
        [:]                    | 'asc'
    }
    class Book {
        String title
    }
}

