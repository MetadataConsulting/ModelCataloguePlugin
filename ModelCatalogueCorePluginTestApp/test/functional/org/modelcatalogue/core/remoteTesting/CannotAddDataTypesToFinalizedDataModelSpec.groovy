package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Ignore
import spock.lang.Stepwise

@Stepwise
@Ignore
class CannotAddDataTypesToFinalizedDataModelSpec extends GebSpec {
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

    def "Navigate to tree view and click on the Data Types"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')

        then:
        at DataTypesPage
    }

    def "Verify that you can not create a new data type"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage

        then:
        !dataTypesPage.isAddItemIconVisible()
    }
}