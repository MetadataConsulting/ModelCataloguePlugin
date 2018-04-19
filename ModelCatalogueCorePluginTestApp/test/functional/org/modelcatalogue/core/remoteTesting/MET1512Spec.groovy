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
class MET1512Spec extends GebSpec {
    @Shared
    String dataModelName = "NEW_TESTING_MODEL"


    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "create new data model"() {
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

    def "select data model acl"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.dataModelPermission()
        then:
        at DataModelPermissionListPage
    }

    def "grant user read only access to created data model"() {
        when:
        DataModelPermissionListPage dataModelPermissionListPage = browser.page DataModelPermissionListPage
        dataModelPermissionListPage.selectDataModal(dataModelName)
        then:
        at DataModelPermissionGrantPage

        when:
        DataModelPermissionGrantPage dataModelPermissionGrantPage = browser.page DataModelPermissionGrantPage
        dataModelPermissionGrantPage.selectUsername("user")
        dataModelPermissionGrantPage.selectPermission("read")
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

    def "login as user"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('user', 'user')

        then:
        at DashboardPage
    }

    def "select data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select(dataModelName)
        then:
        at DataModelPage
    }

    def "check inline edit button is disabled"() {

    }

}