package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Stepwise
@Issue('https://metadata.atlassian.net/browse/MET-1650')
@Title('Remove the imported data model appears in the list of Activity')
@Narrative('''
- Login to model catalogue (gel)
-  Select a draft model
- Navigate to Import By tag
-  Import a data model
- Check that the imported data model appears on the list of activity
- Remove the imported data model
''')
@Ignore
class MET1650Spec extends GebSpec {
    @Shared
    String selectModelToEdit = "Test 1"
    @Shared
    String tagName = 'Clinical Tags'

    def "Login to Model Catalogue"() {
        when: "Login to Model Catalogue as curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: "then you get to DashboardPage"
        at DashboardPage
    }

    def "Select a finalized Data Model"() {
        when: "Selected an Finalized Data Model"
        DashboardPage dashboardPage = browser.page(DashboardPage)
        dashboardPage.select(selectModelToEdit)

        then:
        at DataModelPage
    }

    def "Select Imported Data Models"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Imported Data Models')

        then:
        at DataImportsPage
    }

    def "import model "() {
        when:
        DataImportsPage dataImportsPage = browser.page DataImportsPage
        dataImportsPage.addItem()

        then:
        at DataImportPage

        when:
        DataImportPage dataImportPage = browser.page DataImportPage
        dataImportPage.fillSearchBox(tagName)
        dataImportPage.searchMore()

        then:
        at SearchTagPage

        when:
        SearchTagPage searchTagPage = browser.page SearchTagPage
        println $("h4.list-group-item-heading")*.text()
        println $("h4.list-group-item-heading", text: tagName).text()
        println $("h4.list-group-item-heading", text: tagName).size()
        searchTagPage.searchTag(tagName)

        then:
        at DataImportPage

        when:
        Thread.sleep(2000)
        DataImportPage dataImportPage1 = browser.page DataImportPage
        dataImportPage1.finish()

        then:
        Thread.sleep(2000)
        at DataImportsPage
    }

    def "delete the imported data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Imported Data Models')

        then:
        at DataImportsPage

        when: 'remove the imported data models'
        DataImportsPage dataImportsPage = browser.page DataImportsPage
        dataImportsPage.expand()
        dataImportsPage.remove()

        then: 'click on the ok button'
        at RemoteImportPage

        when: 'verify that imported is removed'
        RemoteImportPage remoteImportPage = browser.page RemoteImportPage
        remoteImportPage.finish()

        then:
        at DataImportsPage
        !dataImportsPage.checkImport?.contains(tagName)
    }
}