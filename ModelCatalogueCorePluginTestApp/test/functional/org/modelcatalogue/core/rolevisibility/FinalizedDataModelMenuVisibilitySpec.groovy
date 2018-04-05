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
        dataModelPage.dropdownMenu.existsCreateNewVersion(browser)
        dataModelPage.dropdownMenu.existsArchive(browser)
        dataModelPage.dropdownMenu.existsAddImport(browser)
        dataModelPage.dropdownMenu.existsCreateNewRelationship(browser)
        dataModelPage.dropdownMenu.existsCreateDataClass(browser)
        dataModelPage.dropdownMenu.existsCreateDataElement(browser)
        dataModelPage.dropdownMenu.existsCreateDataType(browser)
        dataModelPage.dropdownMenu.existsCreateMeasurementUnit(browser)
        dataModelPage.dropdownMenu.existsCreateAsset(browser)
        dataModelPage.dropdownMenu.existsValidationRule(browser)
        dataModelPage.dropdownMenu.existsMerge(browser)
        dataModelPage.dropdownMenu.existsCloneCurrentIntoAnother(browser)
        dataModelPage.dropdownMenu.existsReindexDataModel(browser)

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
        dataModelPage.dropdownMenu.existsCreateNewVersion(browser)
        dataModelPage.dropdownMenu.existsMerge(browser)
        dataModelPage.dropdownMenu.existsCloneCurrentIntoAnother(browser)

        and:
        !dataModelPage.dropdownMenu.existsDelete(browser)
        !dataModelPage.dropdownMenu.existsFinalize(browser)
        !dataModelPage.dropdownMenu.existsArchive(browser)
        !dataModelPage.dropdownMenu.existsDelete(browser)
        !dataModelPage.dropdownMenu.existsAddImport(browser)
        !dataModelPage.dropdownMenu.existsCreateNewRelationship(browser)
        !dataModelPage.dropdownMenu.existsCreateDataClass(browser)
        !dataModelPage.dropdownMenu.existsCreateDataElement(browser)
        !dataModelPage.dropdownMenu.existsCreateDataType(browser)
        !dataModelPage.dropdownMenu.existsCreateMeasurementUnit(browser)
        !dataModelPage.dropdownMenu.existsCreateAsset(browser)
        !dataModelPage.dropdownMenu.existsValidationRule(browser)
        !dataModelPage.dropdownMenu.existsCloneAnotherIntoCurrent(browser)
    }
}