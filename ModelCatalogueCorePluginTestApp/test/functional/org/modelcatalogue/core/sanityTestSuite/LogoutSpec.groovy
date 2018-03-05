package org.modelcatalogue.core.sanityTestSuite

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginPage

class LogoutSpec extends GebSpec {

    def "user is able to logout"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('viewer', 'viewer')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.logout()

        then:
        at HomePage

        when:
        HomePage homePage = browser.page HomePage

        then:
        homePage.loginButton.isDisplayed()

    }
}
