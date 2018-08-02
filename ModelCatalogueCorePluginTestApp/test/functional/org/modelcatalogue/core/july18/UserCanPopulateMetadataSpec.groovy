package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1482')
@Title('User can populate Metadata')
@Stepwise
class UserCanPopulateMetadataSpec extends GebSpec {

    @Shared
    String name = UUID.randomUUID().toString()
    @Shared
    String description = UUID.randomUUID().toString()
    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String NewDataModelName = UUID.randomUUID().toString()
    @Shared
    String dataModelVersion = "1.3"
    @Shared
    String dataModelVersionNote = "versionNote"


    def "login as supervisor"() {
        when: 'login as a supervisor'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "select draft data model and version tag"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search("Test 1")
        dashboardPage.select("Test 1")
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.versions()
        then:
        at VersionsPage

        when:
        VersionsPage versionsPage = browser.page VersionsPage
        then:
        versionsPage.rowsContainText("0.0.1")
        and:
        versionsPage.rowsContainText("Test 1")

        when:
        versionsPage = browser.page VersionsPage
        versionsPage.expandLinkClick()
        versionsPage.editButtonClick()

        then:
        at EditDataModelPage

        when:
        EditDataModelPage editDataModelPage = browser.page EditDataModelPage
        editDataModelPage.fillName(name)
        editDataModelPage.fillDescription(description)
        sleep(5_000)
        editDataModelPage.submitBttn()
        then:
        at VersionsPage

        when:
        versionsPage = browser.page VersionsPage
        then:
        sleep(1_000)
        versionsPage.rowsContainText(name)

        when:
        versionsPage = browser.page VersionsPage
        versionsPage.expandLinkClick()
        versionsPage.editButtonClick()

        then:
        at EditDataModelPage

        when:
        editDataModelPage = browser.page EditDataModelPage
        editDataModelPage.fillName("Test 1")
        editDataModelPage.submitBttn()
        then:
        at VersionsPage
    }

    def "create data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "create data element for data model B"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataModel()
        then:
        at DataModelPage
    }

    def "finalize the data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.finalizedDataModel()
        then:
        at FinalizeDataModelPage

        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.version = dataModelVersion
        finalizeDataModelPage.versionNote = dataModelVersionNote
        sleep(2_000)
        finalizeDataModelPage.submit()
        then:
        at FinalizedDataModelPage

        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.versions()
        then:
        at VersionsPage

        when:
        VersionsPage versionsPage = browser.page VersionsPage
        then:
        versionsPage.rowsContainText(dataModelVersion)
        and:
        versionsPage.rowsContainText(dataModelName)
    }

    def "select draft data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.search("Test 1")
        dashboardPage.select("Test 1")
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.editDataModel()
        dataModelPage.editInputField(NewDataModelName)
        dataModelPage.save()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        then:
        dataModelPage.verifySemanticNumber("NewDataModelName")

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.editDataModel()
        dataModelPage.editInputField("Test 1")
        dataModelPage.save()
        then:
        at DataModelPage
    }


}