package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Stepwise
class MET1925Spec extends GebSpec {

    def "Login as user"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('user', 'user')

        then:
        at DashboardPage
    }

    def "select settings"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        then:
        at DashboardPage
    }

    def "select code version"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.codeversion()
        then:
        at CodeVersionPage
    }

    def "check version"() {
        when:
        CodeVersionPage codeVersionPage = browser.page CodeVersionPage

        then:
        assert codeVersionPage.isGithubLinkDisplayed() == true
    }

}