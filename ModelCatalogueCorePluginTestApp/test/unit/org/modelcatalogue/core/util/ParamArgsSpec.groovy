package org.modelcatalogue.core.util

import spock.lang.Specification

class ParamArgsSpec extends Specification {

    def "ParamArgs to map with as keyword"() {
        given:
        ParamArgs paramArgs = new ParamArgs(order: 'title', sort: 'asc', max: 100, offset: 10)

        expect:
        (paramArgs as Map) == [order: 'title', sort: 'asc', max: 100, offset: 10]
    }
}
