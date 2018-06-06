package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import jdk.nashorn.internal.runtime.SharedPropertyMap
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Stepwise
import spock.lang.Shared
import org.modelcatalogue.core.geb.*

@Issue('https://metadata.atlassian.net/browse/MET-1620')
@Title('Examine that admin can remove an imported model')
@Narrative($/
 - Login to Metadata Exchange As supervisor | Login successful
 - Select the 'Create Data Model' button (plus sign) from the top -right hand menu | Redirected to 'Create Data Model' page
 - Fill form with Name, Catalogue ID, Description and press Save button. | New Data Model is created. Redirected to the new Data Model's main page.
 - Navigate via tree-view navigation panel and select "Imported Data Models" tag | Display panel opens up the 'Imported Data Model' page. Title is '[Data model name] Imports'
 - Select the green plus sign button to import a data model. | Import data model dialogue pop-up box appears. Title is [data model name].
 - In the dialogue box, underneath the title 'Destination' Select the book icon to the left of the search bar to bring up list of Data Models. | Search/List pop-up dialogue box appears containing list of Data Models.
 - Select a Data Model from the list . | Search/list pop-up closes. Selected Data Model name appears in search bar underneath title 'Destination'
 - Click on 'Create Relationship' button to import Data Model | Data Model is imported
 - Verify that imported data model appears in list underneath title '[Data Model Name] Imports ' | Imported Data Model appears in list
 - Click on 'Show More Button' plus sign button to the side of the imported Data model name | More Info panel about imported Data Model opens up.
 - Select the 'Remove' button (the X sign) from the menu on the right hand side in the opened panel | 'Remove Relationship' pop-up appears asking 'Do you really want to remove relation '[Name Here] imports [Imported Data Model]'?
 - Select OK from the 'Remove Relationship' pop up box. | Imported Data Model is removed from list of imported data models (and no longer imported).
 - Check that data model is removed from imported Data Models List | Data Model has been removed from 'Imported Data Models' list
/$)
@Stepwise
class CheckAdminCanDeleteImportedModelSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String importDataModelName = "Cancer Model"

    def "login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "create new data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.description = dataModelDescription
        createDataModelPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "navigate to imported data model"() {
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

    def "remove imported model"() {
        when:
        DataImportsPage dataImportsPage = browser.page DataImportsPage
        dataImportsPage.expandTag()
        dataImportsPage.remove()
        then:
        at RemoveImportPage

        when:
        RemoveImportPage removeImportPage = browser.page RemoveImportPage
        removeImportPage.finish()
        then:
        at DataImportsPage
    }

    def "verify imported model is not shown in list"() {
        when:
        DataImportsPage dataImportsPage = browser.page DataImportsPage
        driver.navigate().refresh()
        then:
        !dataImportsPage.containsData(importDataModelName)
    }
}
