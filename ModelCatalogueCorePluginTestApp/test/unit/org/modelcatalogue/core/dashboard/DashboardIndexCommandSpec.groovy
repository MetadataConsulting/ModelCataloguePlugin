package org.modelcatalogue.core.dashboard

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

@Mock(DashboardController)
class DashboardIndexCommandSpec extends Specification {

    void 'test dataModelId can be null'() {
        given:
        DashboardIndexCommand cmd = new DashboardIndexCommand()

        when:
        cmd.dataModelId = null

        then:
        cmd.validate(['dataModelId'])
    }

    void 'test search can be null'() {
        given:
        DashboardIndexCommand cmd = new DashboardIndexCommand()

        when:
        cmd.search = null

        then:
        cmd.validate(['search'])
    }

    void 'toSearchStatusQuery for blank search returns null'() {
        when:
        DashboardIndexCommand cmd = new DashboardIndexCommand(search: '   ')
        SearchStatusQuery query = cmd.toSearchStatusQuery()

        then:
        query.search == null
    }

    @Unroll
    def "#sort #description for sort"(String sort, boolean expected, String description) {
        given:
        DashboardIndexCommand cmd = new DashboardIndexCommand()

        when:
        cmd.sort = sort

        then:
        cmd.validate(['sort']) == expected

        where:
        sort                | expected
        'name'              | true
        'status'            | true
        'semanticVersion'   | true
        'lastUpdated'       | true
        'foo'               | false
        description = expected ? 'is valid' : 'is not valid value'
    }

    @Unroll
    def "#sort #description for order"(String order, boolean expected, String description) {
        given:
        DashboardIndexCommand cmd = new DashboardIndexCommand()

        when:
        cmd.order = order

        then:
        cmd.validate(['order']) == expected

        where:
        order              | expected
        'asc'             | true
        'desc'            | true
        'foo'             | false
        description = expected ? 'is valid' : 'is not valid value'
    }
}
