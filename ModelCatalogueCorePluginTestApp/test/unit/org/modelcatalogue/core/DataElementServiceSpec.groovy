package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(DataElementService)
class DataElementServiceSpec extends Specification {

    @Unroll
    def "tagId of #params is #tagId"(Map params, String tagId) {
        expect:
        tagId == service.tagIdOfParams(params)

        where:
        params             | tagId
        null               | null
        [:]                | null
        [tag: 'none']      | 'none'
        [tag: 'undefined'] | 'undefined'
        [tag: 'null']      | 'null'
        [tag: 'all']       | null

    }
}
