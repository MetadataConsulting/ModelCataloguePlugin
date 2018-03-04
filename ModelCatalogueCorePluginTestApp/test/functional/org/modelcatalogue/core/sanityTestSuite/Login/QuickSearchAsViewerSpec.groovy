package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelListPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
class QuickSearchAsViewerSpec extends GebSpec {

    def "login to model catalogue"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DataModelListPage

        when:
        DataModelListPage dataModelListPage = browser.page DataModelListPage
        dataModelListPage.dashboard()

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Clinical trial')
        dashboardPage.selectFirst()

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.rightSideTitle.contains('Clinical')
    }
}
