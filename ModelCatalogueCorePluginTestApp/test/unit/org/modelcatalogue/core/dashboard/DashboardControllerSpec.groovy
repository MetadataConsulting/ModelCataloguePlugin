package org.modelcatalogue.core.dashboard

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(DashboardController)
class DashboardControllerSpec extends Specification {

    def "dashboard.index model contains sortQuery"() {
        given:
        controller.dashboardService = Mock(DashboardService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('sortQuery')
        model['sortQuery'].order == 'asc'
        model['sortQuery'].sort == 'name'
    }

    def "dashboard.index model contains paginationQuery"() {
        given:
        controller.dashboardService = Mock(DashboardService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('paginationQuery')
        model['paginationQuery'].max == 25
        model['paginationQuery'].offset == 0
    }

    def "dashboard.index model contains search"() {
        given:
        controller.dashboardService = Mock(DashboardService)

        when:
        params.search = 'Cancer'
        Map model = controller.index()

        then:
        model.keySet().contains('search')
        model['search'] == 'Cancer'
    }

    def "dashboard.index model contains status"() {
        given:
        controller.dashboardService = Mock(DashboardService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('status')
        model['status'] == DashboardDropdown.ACTIVE
    }

    def "dashboard.index model contains models"() {
        given:
        controller.dashboardService = Mock(DashboardService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('models')
        model['models'] == []
    }

    def "dashboard.index model contains total"() {
        given:
        controller.dashboardService = Mock(DashboardService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('total')
        model['total'] == 0
    }

    def "findAllBySearchStatusQuery and count are invoked once"() {
        given:
        controller.dashboardService = Mock(DashboardService)

        when:
        controller.index()

        then:
        1 * controller.dashboardService.findAllBySearchStatusQuery(_, _, _)
        1 * controller.dashboardService.countAllBySearchStatusQuery(_)
    }
}
