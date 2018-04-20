package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.codehaus.groovy.transform.stc.SharedVariableCollector
import org.junit.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Stepwise
class MET1727Spec extends GebSpec {

    @Shared
    String username = "user"

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "open settings dropdown"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.users()
        then:
        at UserSearchPage
    }

    def "search user"() {
        when:
        UserSearchPage userSearchPage = browser.page UserSearchPage
        userSearchPage.fillUser(username)
        Thread.sleep(5000)
        userSearchPage.search()
        then:
        at UserSearchPage
    }

    def "select user"() {
        when:
        UserSearchPage userSearchPage = browser.page UserSearchPage
        userSearchPage.selectUser()

        then:
        at UserEditPage

    }

    def "select user role"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        userEditPage.clickRoles()
        then:
        at UserEditPage
    }

    def "assign user roles"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        userEditPage.assignMetadataCuratorRole()
        userEditPage.update()
        then:
        at UserEditPage
    }

    def "logout"() {
        when:
        UserEditPage userEditPage = browser.page UserEditPage
        userEditPage.logout()
        then:
        at HomePage
    }

    def "Login as user"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('user', 'user')

        then:
        at DashboardPage
    }

    def "check import menu"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage

        then:
        dashboardPage.nav.importMenuLink.displayed
    }

}