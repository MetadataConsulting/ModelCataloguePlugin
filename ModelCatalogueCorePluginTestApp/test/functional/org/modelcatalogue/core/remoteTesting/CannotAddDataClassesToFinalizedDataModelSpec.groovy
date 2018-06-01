package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Stepwise
@Ignore
class CannotAddDataClassesToFinalizedDataModelSpec extends GebSpec {
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

    def "Navigate to tree view and click on the Data Element"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Classes')

        then:
        at DataClassesPage
    }

    def "Verify that you can not create a new data element"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage

        then:
        !dataClassesPage.isAddItemIconVisible()
    }
}