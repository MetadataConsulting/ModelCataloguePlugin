package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Stepwise
class MET2014Spec extends GebSpec {

    @Shared
    String dataModelOneName = "DATA_MODEL_ONE"
    @Shared
    String dataModelTwoName = "DATA_MODEL_TWO"
    @Shared
    String dataModelThreeName = "DATA_MODEL_THREE"
    @Shared
    String dataTypeOneName = "DATATYPE_ONE"
    @Shared
    String dataTypeTwoName = "DATATYPE_TWO"
    @Shared
    String dataTypeThreeName = "DATATYPE_THREE"
    @Shared
    String dataElementName = "DATA_ELEMENT"

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "create first data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelOneName
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "create first data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeOneName
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "create second data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelTwoName
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "create second data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeTwoName
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "create third data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelThreeName
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "create third data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
        createDataTypePage.name = dataTypeThreeName
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "select data model ACL"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.dataModelPermission()
        then:
        at DataModelPermissionListPage
    }

    def "grant admin right to curator for first data model"() {
        when:
        DataModelPermissionListPage dataModelPermissionListPage = browser.page DataModelPermissionListPage
        dataModelPermissionListPage.selectDataModal(dataModelOneName)
        then:
        at DataModelPermissionGrantPage

        when:
        DataModelPermissionGrantPage dataModelPermissionGrantPage = browser.page DataModelPermissionGrantPage
        dataModelPermissionGrantPage.selectUsername("curator")
        dataModelPermissionGrantPage.selectPermission("administration")
        dataModelPermissionGrantPage.grantPermission()
        then:
        at DataModelPermissionGrantPage
    }

    def "grant admin right to curator for second data model"() {
        when:
        DataModelPermissionListPage dataModelPermissionListPage = to DataModelPermissionListPage
        dataModelPermissionListPage.selectDataModal(dataModelTwoName)
        then:
        at DataModelPermissionGrantPage

        when:
        DataModelPermissionGrantPage dataModelPermissionGrantPage = browser.page DataModelPermissionGrantPage
        dataModelPermissionGrantPage.selectUsername("curator")
        dataModelPermissionGrantPage.selectPermission("administration")
        dataModelPermissionGrantPage.grantPermission()
        then:
        at DataModelPermissionGrantPage
    }

    def "logout as supervisor"() {
        when:
        DataModelPermissionGrantPage dataModelPermissionGrantPage = browser.page DataModelPermissionGrantPage
        dataModelPermissionGrantPage.nav.userMenu()
        dataModelPermissionGrantPage.nav.logout()
        then:
        at HomePage
    }

    def "login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "select first data model created"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select(dataModelOneName)
        then:
        at DataModelPage
    }

    def "go to data element page"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Elements")
        then:
        at DataElementsPage
    }

    def "create data element"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = dataElementName
        createDataElementPage.searchMore()
        createDataElementPage.showAllDataType()
        then:
        at SearchAllModalPage
    }

    def "verify data element from third model is not clonable"() {
        when:
        SearchAllModalPage searchAllModalPage = browser.page SearchAllModalPage
        searchAllModalPage.searchDataType(dataTypeThreeName)
        Thread.sleep(1000)
        then:
        searchAllModalPage.noResultFound()
    }

    def "verify data element from second model is clonable"() {
        when:
        SearchAllModalPage searchAllModalPage = browser.page SearchAllModalPage
        searchAllModalPage.clearSearchField()
        searchAllModalPage.searchDataType(dataTypeTwoName)
        Thread.sleep(1000)
        then:
        !searchAllModalPage.noResultFound()
    }

    def "clone data element of second model"() {
        when:
        SearchAllModalPage searchAllModalPage = browser.page SearchAllModalPage
        searchAllModalPage.selectDataType()
        Thread.sleep(1000)
        then:
        at ImportOrCloneModalPage

        when:
        ImportOrCloneModalPage importOrCloneModalPage = browser.page ImportOrCloneModalPage
        importOrCloneModalPage.cloneElement()
        Thread.sleep(1000)
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at DataElementsPage
    }

}