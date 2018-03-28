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
class MET1626Spec extends GebSpec {
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000

    @Shared
    String dataModelName = 'Test 1'
    @Shared
    String tagName = 'Clinical Tags'

    def "Login to model catalogue"() {
        when: "Login to Model Catalogue as curator"
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: "then you get to DashboardPage"
        at DashboardPage
    }

    def "Select a draft Data Model"() {
        when: "Selected an draft Data Model"
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select(dataModelName)

        then: "Data Model Page Should Open"
        at DataModelPage
    }

    def "navigate to the top menu and select create relationship"() {
        when: 'navigate to createRelationship page'
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.dropdown.addImport()

        then: 'verify that the text Destination is displayed'
        at DropDownImportPage
    }

    def "select a data model"() {
        when: 'select a model'
        DropDownImportPage dropDownImportPage = browser.page DropDownImportPage
        dropDownImportPage.fillSearchBox(tagName)
        dropDownImportPage.searchMore()

        then:
        at SearchTagPage

        when:
        SearchTagPage searchTagPage = browser.page SearchTagPage
        println $("h4.list-group-item-heading", text: tagName).text()
        println $("h4.list-group-item-heading", text: tagName).size()
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
        DataModelPage dashboardPage = browser.page DataModelPage
        dashboardPage.treeView.select("Imported Data Models")

        then:
        at DataImportsPage
    }
}