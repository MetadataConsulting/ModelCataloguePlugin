package org.modelcatalogue.core.version

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CodeVersionPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Ignore
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1925')
@Title('Version verification')
@Narrative('''
- Login to model catalogue | As User
- On the top right hand menu click on settings button to open drop-down menu
- Scroll down and select the Code Version option | you are redirected to a new page/window with the version number
- Check that the model catalogue carries the correct version
''')
@Stepwise
@Ignore
class VersionVerificationSpec extends GebSpec {

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
        codeVersionPage.isGithubLinkDisplayed()
    }

}
