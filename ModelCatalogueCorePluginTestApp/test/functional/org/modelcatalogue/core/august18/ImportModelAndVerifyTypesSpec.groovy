package org.modelcatalogue.core.august18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1484')
@Title('Import Model and Verify Types')
@Stepwise
class ImportModelAndVerifyTypesSpec extends GebSpec {

    @Shared
    String dataTypeName = UUID.randomUUID().toString()
    @Shared
    String importDataModelName = "Test 2"

    def "login as supervisor"() {
        when: 'login as a supervisor'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "Select draft model and create relationship"(){
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search("Test 1")
        dashboardPage.select("Test 1")
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.importedDataModels()
        then:
        at DataImportsPage
    }

    def "import data model"() {
        when:
        DataImportsPage dataImportsPage = browser.page DataImportsPage
        dataImportsPage.addItem()
        then:
        at DataImportPage

        when:
        DataImportPage dataImportPage = browser.page DataImportPage
        dataImportPage.searchMore()
        then:
        at SearchTagPage

        when:
        SearchTagPage searchTagPage = browser.page SearchTagPage
        searchTagPage.searchTag(importDataModelName)
        then:
        at DataImportPage

        when:
        dataImportPage = browser.page DataImportPage
        dataImportPage.finish()
        then:
        at DataImportsPage
    }

    def "verify imported data model is shown in list"() {
        when:
        DataImportsPage dataImportsPage = browser.page DataImportsPage
        then:
        dataImportsPage.containsData(importDataModelName)
    }

}