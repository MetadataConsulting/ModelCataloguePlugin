package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataClassPage
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataClassesPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.FinalizeDataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1441')
@Title('Verify that a curator is not able to delete a finalized Data Model after creation')
@Narrative('''
- Login to Model Catalogue
- Click on Create and fill the form
- Navigate to the top menu and click on the Data Model link
- Scroll down and click on finalize
- Finalized the data model 
- On the top menu, click on the data model link
- Click on the delete link
''')
@Ignore
class CannotDeleteFinalizedDataModelSpec extends GebSpec {
    @Shared
    String uuid = UUID.randomUUID().toString()

    def "'Verify that a curator is not able to delete a finalized Data Model after creation'"() {
        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: 'click the create data model button'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then: 'the create data model form is opened'
        at CreateDataModelPage

        when: 'enter a random name for the data model and click create'
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = uuid
        createDataModelPage.submit()

        then: 'data model page for the new data model is displayed'
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        String dataModelUrl = browser.driver.currentUrl

        then: 'the random name we entered in the form is displayed in the data mdoel title'
        dataModelPage.titleContains uuid

        when: 'select Data Classes in the tree'
        dataModelPage.treeView.select('Data Classes')

        then: 'You get to the data classes page'
        at DataClassesPage

        when: 'click the create data class button'
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()

        then: 'you are in the create data class page'
        at CreateDataClassPage

        when: 'fill the Data Class Form and save it'
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = "NEW_TESTING_MODEL "
        createDataClassPage.modelCatalogueId = "${UUID.randomUUID()}"
        createDataClassPage.description = 'THIS IS MY DATA CLASS'
        createDataClassPage.finish()
        createDataClassPage.exit()

        then: 'your are redirected to the data classes page'
        at DataClassesPage

        when:
        dataClassesPage = browser.page DataClassesPage

        then: 'there is one row in the data classes list'
        dataClassesPage.count() == 1

        when: "Click on the data model name"
        browser.go dataModelUrl

        then: 'you get to the data model page'
        at DataModelPage

        when: 'Select finalize in the data model options menu'
        dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdown.finalize()

        then: 'The Finalize Data Model Pops up'
        at FinalizeDataModelPage

        when: 'Fill the finalized form and submit'
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.versionNote = 'THIS IS THE VERSION NOTE'
        finalizeDataModelPage.submit()

        then: 'you are in the data model page'
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage

        then: 'The title of the data model displays "finalized" texts'
        dataModelPage.titleContains("$uuid (0.0.1) finalized")

        when: "Click on the Main Menu"
        dataModelPage.dropdown()

        then:  "No Option for the delete. Delete is disabled"
        !dataModelPage.dropdown.existsDelete()
    }
}