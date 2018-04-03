package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.CreateDataClassPage
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataClassesPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.FinalizeDataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.CatalogueAction
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
@Stepwise
class MET1441Spec extends GebSpec {
    @Shared
    String uuid = UUID.randomUUID().toString()

    def "Login to Model Catalouge"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = uuid
        createDataModelPage.submit()

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.titleContains uuid

        when:
        dataModelPage.treeView.select('Data Classes')

        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()

        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = "NEW_TESTING_MODEL "
        createDataClassPage.modelCatalogueId = "${UUID.randomUUID()}"
        createDataClassPage.description = 'THIS IS MY DATA CLASS'
        createDataClassPage.finish()
        createDataClassPage.exit()

        then:
        at DataClassesPage

        and:
        ((DataClassesPage) browser.page(DataClassesPage)).count() == 1
    }

    def "Finalized the Data Model"() {
        when: "Click on the Main Menu and finalized data model"
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.treeView.dataModel()

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdown.finalize()

        then:
        at FinalizeDataModelPage

        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.versionNote = 'THIS IS THE VERSION NOTE'
        finalizeDataModelPage.submit()

        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.titleContains("$uuid (0.0.1) finalized")

        when: "Click on the Main Menu"
        dataModelPage.dropdown()

        then:  "No Option for the delete. Delete is disabled"
        !dataModelPage.dropdown.existsDelete()
    }
}