package org.modelcatalogue.core.suiteA

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginModalPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore

class UsersDontSeeUnauthorizedDataModelsSpec extends GebSpec {

    def "curators see more models than viewers"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        int dataModelVisibleToCurators = dashboardPage.count()
        dashboardPage.nav.userMenu()
        dashboardPage.nav.logout()

        then:
        at HomePage

        when:
        HomePage homePage = to HomePage
        homePage.login()

        then:
        at LoginModalPage

        when:
        LoginModalPage loginModalPage = browser.page LoginModalPage
        loginModalPage.username = 'user'
        loginModalPage.password = 'user'
        loginModalPage.login()

        then:
        at DashboardPage

        when:
        dashboardPage = browser.page DashboardPage

        then:
        dataModelVisibleToCurators != dashboardPage.count()
    }
}
