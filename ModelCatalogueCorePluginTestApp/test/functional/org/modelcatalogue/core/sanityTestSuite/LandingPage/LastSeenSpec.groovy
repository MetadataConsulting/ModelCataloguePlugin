package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*

//@IgnoreIf({ !System.getProperty('geb.env') })
class LastSeenSpec extends GebSpec {

    def "login to model catalogue "() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.activity()

        then:
        at LastSeenPage
    }
}
