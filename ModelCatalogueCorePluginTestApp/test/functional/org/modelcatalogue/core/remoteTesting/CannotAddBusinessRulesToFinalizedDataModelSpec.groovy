package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.Stepwise

@Stepwise
@Ignore
class CannotAddBusinessRulesToFinalizedDataModelSpec extends GebSpec {
    def "Login to Model Catalogue"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "Select a Finalized Model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Cancer Model')

        then:
        at DataModelPage
    }

    def "Navigate to tree view and click on the Business Rules"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Business Rules')

        then:
        at BusinessRulesPage
    }

    def "Verify that you can not create a new Business Rules"() {
        when:
        BusinessRulesPage businessRulesPage = browser.page BusinessRulesPage

        then:
        !businessRulesPage.isAddItemIconVisible()
    }
}