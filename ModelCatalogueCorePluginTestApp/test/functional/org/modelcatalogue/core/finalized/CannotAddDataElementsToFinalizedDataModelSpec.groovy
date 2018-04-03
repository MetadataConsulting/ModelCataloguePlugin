package org.modelcatalogue.core.finalized

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1566')
@Title('Check that When Data Model is finalized,you are not able to add new elements')
@Narrative('''
- Login to Model Catalogue
- Select a Finalized Model
- Navigate to tree view and click on the Data Element
- Verify that you can not create a new data element
''')
@Stepwise
class CannotAddDataElementsToFinalizedDataModelSpec extends GebSpec {
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
        dataModelPage.treeView.select('Data Elements')

        then:
        at DataElementsPage
    }

    def "Verify that you can not create a new data element"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage

        then:
        !dataElementsPage.isAddItemIconVisible()
    }
}