package org.modelcatalogue.core.finalized

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1566')
@Title('Check that When Data Model is finalized,you are not able to add new elements')
@Narrative($/
 - Login to Metadata Exchange as curator | Login Successful
 - Click on the 'Create New Data Model' (plus sign) button in the top right hand menu. | Redirected to Create Data Model page
 - Populate form fields with Name, Catalogue ID and description. Click the Save button. | Data Model is created. Redirected to new Data Model main page
 - Select the 'Data Model' menu button from the top left hand menu. | Data Model menu button drop-down appears
 - Select option to 'Finalize' data model | Finalize Data Model pop-up appears.
 - Click OK button within the 'Finalize' data model pop up. | Data Model is finalized. Verify that it is shown as finalized in display panel.
 - In the now- finalised Data Model, Navigate using the tree-navigation and click on the Data Elements tag | Data Elements page is shown in display panel. 'Active Data Elements' is title
 - Verify that you can not create a new data element. There is no plus button or top-left hand 'Data Elements' menu button present. | No button / way to create new Data Element
/$)

@Stepwise
class CannotAddElementToFinalizedModelSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataModelCatalogueId = UUID.randomUUID().toString()
    @Shared
    String dataModelDescription = "description"
    @Shared
    String version = "1.1"
    @Shared
    String versionNote = "versionNote"

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')
        then:
        at DashboardPage
    }

    def "Create data model and filling data model form"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage
        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.modelCatalogueId = dataModelCatalogueId
        createDataModelPage.description = dataModelDescription
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "finalize data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataModel()
        then:
        at DataModelPage
        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.finalizedDataModel()
        then:
        at FinalizeDataModelPage
        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.version = version
        finalizeDataModelPage.setVersionNote(versionNote)
        sleep(1_000)
        finalizeDataModelPage.submit()
        sleep(2_000)
        then:
        waitFor { at FinalizedDataModelPage }
        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage
    }


    def "Check data model status is diaplayimg as  finalized "() {

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.checkFinalizedStatus()
        then:
        at DataModelPage
    }

    def "Select data elements"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Elements")
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        then:
        !dataElementsPage.isAddItemIconVisible()

        when:
        dataElementsPage = browser.page DataElementsPage
        then:
        !dataElementsPage.iscreateDateElementLinkVisible()

    }
}
