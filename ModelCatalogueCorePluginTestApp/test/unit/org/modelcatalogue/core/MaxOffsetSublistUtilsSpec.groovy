package org.modelcatalogue.core

import spock.lang.Shared
import spock.lang.Specification

class MaxOffsetSublistUtilsSpec extends Specification {

    @Shared
    List<Book> bookList = [
            new Book(title: 'Grails 3 - Step by Step'),
            new Book(title: 'Pratical Grails 3'),
            new Book(title: 'Falando de Grails'),
            new Book(title: 'Grails Goodness Notebook'),
            new Book(title: 'The Definitive Guide to Grails 2'),
            new Book(title: 'Grails in Action'),
            new Book(title: 'Grails 2: A Quick-Start Guide'),
    ]

    def "sublist with empty map, returns original list"() {
        when:
        List<Book> result = MaxOffsetSublistUtils.subList(bookList, [:])

        then:
        result
        result.size() == bookList.size()
        result[0].title == 'Grails 3 - Step by Step'
        result[1].title == 'Pratical Grails 3'
        result[2].title == 'Falando de Grails'
        result[3].title == 'Grails Goodness Notebook'
        result[4].title == 'The Definitive Guide to Grails 2'
        result[5].title == 'Grails in Action'
        result[6].title == 'Grails 2: A Quick-Start Guide'
    }

    def "sublist with max of two elements and no offset"() {
        when:
        List<Book> result = MaxOffsetSublistUtils.subList(bookList, [max: 2])

        then:
        result
        result.size() == 2
        result[0].title == 'Grails 3 - Step by Step'
        result[1].title == 'Pratical Grails 3'
    }

    def "sublist with max of two elements and an offset"() {
        when:
        List<Book> result = MaxOffsetSublistUtils.subList(bookList, [max: 2, offset: 2])

        then:
        result
        result.size() == 2
        result[0].title == 'Falando de Grails'
        result[1].title == 'Grails Goodness Notebook'
    }

    def "sublist with max of two elements and an offset, max + offset > list size"() {
        when:
        List<Book> result = MaxOffsetSublistUtils.subList(bookList, [max: 3, offset: 6])

        then:
        result
        result.size() == 1
        result[0].title == 'Grails 2: A Quick-Start Guide'
    }

    class Book {
        String title
    }
}


