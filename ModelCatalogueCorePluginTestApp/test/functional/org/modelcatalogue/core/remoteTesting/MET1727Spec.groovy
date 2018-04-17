package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1469')
@Title('Verify that the history is populated according to activity made on a model')
@Narrative('''
- Login as curator
- Select any Data Model
- Create a data class
- Create a data element
- Edit the created data class and save
- Create a new Tag
''')
@Stepwise
class MET1727Spec extends GebSpec {

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
//        userSearchPage.fillUser("user") giving error due to autocomplete
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