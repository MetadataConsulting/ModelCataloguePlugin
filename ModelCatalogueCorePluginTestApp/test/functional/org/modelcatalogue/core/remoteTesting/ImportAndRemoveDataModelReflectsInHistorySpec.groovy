package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title

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
//@Ignore
class ImportAndRemoveDataModelReflectsInHistorySpec extends GebSpec {

    def "https://metadata.atlassian.net/browse/MET-1650"() {
        given:
        final String selectModelToEdit = "Test 1"
        final String tagName = 'Clinical Tags'

        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: 'Selected an Finalized Data Model'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select(selectModelToEdit)

        then:
        at DataModelPage

        when: 'Select Imported Data Models'
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Imported Data Models')

        then:
        at DataImportsPage

        when: 'import model'
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
        searchTagPage.searchTag(tagName)

        then:
        at DataImportPage

        when:
        Thread.sleep(2000)
        dataImportPage = browser.page DataImportPage
        dataImportPage.finish()

        then:
        Thread.sleep(2000)
        at DataImportsPage

        when: 'delete the imported data model'
        dataImportsPage = browser.page DataImportsPage
        dataImportsPage.expandTag()
        waitFor { dataImportsPage.isRemoveButtonVisible() }
        dataImportsPage.remove()

        then: 'click on the ok button'
        at RemoveImportPage

        when: 'verify that imported is removed'
        RemoveImportPage remoteImportPage = browser.page RemoveImportPage
        remoteImportPage.finish()

        then:
        at DataImportsPage
        !dataImportsPage.checkImport?.contains(tagName)
    }
}