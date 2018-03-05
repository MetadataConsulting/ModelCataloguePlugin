package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.IgnoreIf
import spock.lang.Unroll

@IgnoreIf({ !System.getProperty('geb.env') })
class CodeVersionSpec extends GebSpec {

    @Unroll
    def "#username is able to see ModelCatalogueVersion"(String username, String password) {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login(username, password)

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.codeversion()

        then:
        at CodeVersionPage

        when:
        CodeVersionPage codeVersionPage = browser.page CodeVersionPage

        then:
        codeVersionPage.isGithubLinkDisplayed()

        where:
        username     | password
        'supervisor' | 'supervisor'
        'curator'    | 'curator'
        'viewer'     | 'viewer'
    }
}
