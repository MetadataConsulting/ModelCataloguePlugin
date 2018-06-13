package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.LoginPage

class LoginCuratorSpec extends GebSpec {

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("curator", "curator")
        then:
        at DashboardPage
    }
}
