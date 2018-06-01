package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.IgnoreIf
import spock.lang.Ignore

@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
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
