package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1626')
@Title('Check that a curator can Add Data Model Import from Data Model tag')
@Narrative('''
- Login to model catalogue
- Select a draft model
- Navigate to the top menu and select data model
- Scroll down and select Add Data Model Import
- Select a data model and import
- Check that data model is imported
''')
@Ignore
class CanImportDataModelSpec extends GebSpec {
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000

    def "Check that a curator can Add Data Model Import from Data Model tag"() {
        given:
        final String dataModelName = 'Test 1'
        final String tagName = 'Clinical Tags'

        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: 'click the create data model button'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select(dataModelName)

        then: "Data Model Page Should Open"
        at DataModelPage

        when: 'navigate to createRelationship page'
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdownMenu.addImport()

        then: 'verify that the text Destination is displayed'
        at DropDownImportPage

        when: 'select a model'
        DropDownImportPage dropDownImportPage = browser.page DropDownImportPage
        dropDownImportPage.fillSearchBox(tagName)
        dropDownImportPage.searchMore()

        then:
        at SearchTagPage

        when:
        SearchTagPage searchTagPage = browser.page SearchTagPage
        searchTagPage.searchTag(tagName)

        then:
        at DropDownImportPage

        when:
        Thread.sleep(2000)
        DropDownImportPage dropDownImportPage1 = browser.page DropDownImportPage
        dropDownImportPage1.finish()

        then:
        Thread.sleep(2000)
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Imported Data Models")

        then:
        at DataImportsPage
    }
}