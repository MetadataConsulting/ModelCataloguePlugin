package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelAclPermissionsPage
import org.modelcatalogue.core.geb.DataModelAclPermissionsShowPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.FinalizeDataModelPage
import org.modelcatalogue.core.geb.FinalizedDataModelPage
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginModalPage
import org.modelcatalogue.core.geb.LoginPage

class FinalizedDataModelMenuVisibilitySpec extends GebSpec {

    def "login, add new data model, finalize it"() {
        given:
        final String uuid = UUID.randomUUID().toString()

        when: 'user logs in'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then: 'user is redirected to Dashboard'
        at DashboardPage

        when: 'user clicks in the create new data model button'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then: 'create data model form is open'
        at CreateDataModelPage

        when: 'Fill the form'
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        final String dataModelName = "New Data Model $uuid"
        createDataModelPage.name = dataModelName
        createDataModelPage.submit()

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.finalizedDataModel()
        Thread.sleep(1000)

        then: 'The Finalize Data Model Pops up'
        at FinalizeDataModelPage

        when: 'Fill the finalized form and submit'
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.versionNote = 'THIS IS THE VERSION NOTE'
        finalizeDataModelPage.submit()

        then:
        at FinalizedDataModelPage

        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()

        then: 'you are in the data model page'
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()

        then:
        true // TODO Implement this
//        dataModelPage.dropdownMenu.existsCreateNewVersion()
//        dataModelPage.dropdownMenu.existsArchive()
//        dataModelPage.dropdownMenu.existsAddImport()
//        dataModelPage.dropdownMenu.existsCreateNewRelationship()
//        dataModelPage.dropdownMenu.existsCreateDataClass()
//        dataModelPage.dropdownMenu.existsCreateDataElement()
//        dataModelPage.dropdownMenu.existsCreateDataType()
//        dataModelPage.dropdownMenu.existsCreateMeasurementUnit()
//        dataModelPage.dropdownMenu.existsCreateAsset()
//        dataModelPage.dropdownMenu.existsValidationRule()
//        dataModelPage.dropdownMenu.existsMerge()
//        dataModelPage.dropdownMenu.existsCloneCurrentIntoAnother()
//        dataModelPage.dropdownMenu.existsReindexDataModel()

        when:
        dashboardPage = to DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.dataModelPermission()

        then:
        at DataModelAclPermissionsPage

        when:
        DataModelAclPermissionsPage permissionsPage = browser.page DataModelAclPermissionsPage
        permissionsPage.select(dataModelName)

        then:
        at DataModelAclPermissionsShowPage

        when:
        DataModelAclPermissionsShowPage showPermissionsShowPage = browser.page DataModelAclPermissionsShowPage
        showPermissionsShowPage.grant('curator', 'administration')

        then:
        showPermissionsShowPage.count() == 2

        when:
        showPermissionsShowPage.nav.userMenu()
        showPermissionsShowPage.nav.logout()

        then:
        at HomePage

        when:
        HomePage homePage = to HomePage
        homePage.login()

        then:
        at LoginModalPage

        when:
        LoginModalPage loginModalPage = browser.page LoginModalPage
        loginModalPage.username = 'curator'
        loginModalPage.password = 'curator'
        loginModalPage.login()

        then:
        at DashboardPage

        when:
        dashboardPage = browser.page DashboardPage
        dashboardPage.select(dataModelName)

        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()

        then:
        true // uncomment this
//        dataModelPage.dropdownMenu.existsCreateNewVersion()
//        dataModelPage.dropdownMenu.existsMerge()
//        dataModelPage.dropdownMenu.existsCloneCurrentIntoAnother()

        and:
        !dataModelPage.dropdownMenu.existsDelete()
        !dataModelPage.dropdownMenu.existsFinalize()
        !dataModelPage.dropdownMenu.existsArchive()
        !dataModelPage.dropdownMenu.existsDelete()
        !dataModelPage.dropdownMenu.existsAddImport()
        !dataModelPage.dropdownMenu.existsCreateNewRelationship()
        !dataModelPage.dropdownMenu.existsCreateDataClass()
        !dataModelPage.dropdownMenu.existsCreateDataElement()
        !dataModelPage.dropdownMenu.existsCreateDataType()
        !dataModelPage.dropdownMenu.existsCreateMeasurementUnit()
        !dataModelPage.dropdownMenu.existsCreateAsset()
        !dataModelPage.dropdownMenu.existsValidationRule()
        !dataModelPage.dropdownMenu.existsCloneAnotherIntoCurrent()
    }
}