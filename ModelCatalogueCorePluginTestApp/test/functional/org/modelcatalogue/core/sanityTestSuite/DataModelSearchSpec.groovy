package org.modelcatalogue.core.sanityTestSuite

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import spock.lang.Unroll

@IgnoreIf({ !System.getProperty('geb.env') })
class DataModelSearchSpec extends GebSpec {

    @Unroll
    def "#username user is able to search for a data model and select it"(String username, String password) {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Cancer Model')
        dashboardPage.selectFirst()

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.rightSideTitle.contains('Cancer Model')

        where:
        username     | password
        'supervisor' | 'supervisor'
        'curator'    | 'curator'
        'user'       | 'user'
    }
}
