
package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1628')
@Title('Check that a Curator is not able to create a data class in finalised model')
@Narrative($/
 - Login to Metadata Exchange As Curator | Login successful.
 - Click on the 'Create Data Model' button (plus sign) in the top right menu | Redirected to 'Create Data Model' page
 - Fill form with Name, Catalogue ID, Description and click the Save button. | New Data Model is created. Redirected to new Data Models main page in display panel.
 - Using tree-navigation panel, select Data Classes tag. | Redirected in display panel to Active Data Classes page. Title should be 'Active Data Classes'.
 - Click on the Create New (green plus sign) button in the display panel under the 'Active Data Classes' title. | Create new Data Class Wizard pop-up appears. Title is 'Data Class Wizard'.
 - Fill Data Class Wizard form with Name, Catalogue ID , description. and click the green save button ( green tick) in the top right hand of the Data Class Wizard. | Data Class is created. Data Class Wizard gives options to Create Another or Close.
 - Select to close Data Class Wizard. | Data Class Wizard is closed. New Data Class is present in list under 'Active Data Classes' .
 - Using tree panel navigate to the Data Model name and select to go back to the Data Model main page. | Display panel redirects to Data Model main page.
 - Navigate to top left hand menu. Select the 'Data Model' menu button | Data Model menu drop-down appears
 - From Data Model menu drop-down, select option to 'Finalize' model. | Finalise model dialogue pop-up appears.
 - Fill in 'Finalise Data Model' pop-up with Semantic Version number and revision notes. Click on 'Finalize' button. | 'Finalizing' process dialogue box appears
 - Wait until text in messages panel ends with 'COMPLETED SUCCESSFULLY'. Click 'Hide' on the 'Finalizing' process box | Data Model is finalized. Redirected to main page of Finalized data model in both display and tree navigation panel
 - Using tree navigation panel, select the Data Classes tag. | Redirected in display panel to Active Data Classes page. Title should be 'Active Data Classes'.
 - Verify that (In Active Data Classes) under the list of Data Classes, there is no 'Create new' green plus button . | No option to create new data class.
 - Verify that there is no left hand menu and no 'Data Class' menu button | No Data Class menu button present in Finalized version of the Data Model
/$)

@Stepwise
class CuratorCannotCreateClassInFinalizedModelSpec extends GebSpec {

    @Shared
    String dataPageName = UUID.randomUUID().toString()
    @Shared
    String dataPageId = UUID.randomUUID().toString()
    @Shared
    String dataPageDescription = "description"
    @Shared
    String dataClassName = UUID.randomUUID().toString()
    @Shared
    String dataClassId = UUID.randomUUID().toString()
    @Shared
    String dataClassDescription = "description"
    @Shared
    String version = "1.1"
    @Shared
    String versionNote = "versionNote"


    def "Login as curator"() {
        when:
        sleep(3_000)
        LoginPage loginPage = to LoginPage
        sleep(8_000)
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "Create data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage
    }

    def "Filling form"() {
        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = dataPageName
        createDataModelPage.modelCatalogueId = dataPageId
        createDataModelPage.description = dataPageDescription
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "Select data class tag and fill data class wizard"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        then:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        assert "Active Data Classes" == dataClassesPage.titleText().trim()

        when:
        dataClassesPage = browser.page DataClassesPage
        dataClassesPage.addItemIcon.click()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName
        createDataClassPage.modelCatalogueId = dataClassId
        createDataClassPage.description = dataClassDescription
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "Navigate to data Model main page "() {
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
        finalizeDataModelPage.submit()
        then:
        at FinalizedDataModelPage

        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage
    }

    def "Redirect to data class"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        then:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        assert "Active Data Classes" == dataClassesPage.titleText().trim()
    }

    def "Checking for create button"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        then:
        assert !dataClassesPage.isAddItemIconVisible()
    }
}