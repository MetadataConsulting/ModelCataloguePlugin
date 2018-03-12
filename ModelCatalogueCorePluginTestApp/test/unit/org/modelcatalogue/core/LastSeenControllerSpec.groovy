package org.modelcatalogue.core

import grails.test.mixin.TestFor
import org.modelcatalogue.core.persistence.UserAuthenticationGormService
import spock.lang.Specification

@TestFor(LastSeenController)
class LastSeenControllerSpec extends Specification {

    def "index model contains userAuthenticationList"() {
        given:
        controller.userAuthenticationGormService = Mock(UserAuthenticationGormService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('userAuthenticationList')
    }

    def "index model contains total"() {
        given:
        controller.userAuthenticationGormService = Mock(UserAuthenticationGormService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('total')
    }

    def "index model contains sortQuery"() {
        given:
        controller.userAuthenticationGormService = Mock(UserAuthenticationGormService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('sortQuery')
    }

    def "index model contains paginationQuery"() {
        given:
        controller.userAuthenticationGormService = Mock(UserAuthenticationGormService)

        when:
        Map model = controller.index()

        then:
        model.keySet().contains('paginationQuery')
    }
}
