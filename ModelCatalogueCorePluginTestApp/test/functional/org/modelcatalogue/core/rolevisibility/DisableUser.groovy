package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DataModelPage

@Issue('https://metadata.atlassian.net/browse/MET-1728')
@Title('Disable a user')
@Narrative('''
 - Login to Metadata Exchange as curator | Login successful
 - Select the (plus) 'Create New Data Model' button from the top right hand menu | Redirected to 'Create Data Model' page
 - Fill in the Name, Catalogue ID, Description and click Save | New Data Model is created
 - Log out of Metadata Exchange | Log out successful
 - Login to Metadata Exchange as supervisor | Login successful
 - Select a data model that was created by the Curator | Redirected to the main page of the Data Model
 - Scroll down on the main display panel and select the Activity tab | Activity tab is open
 - In the Activity tab click on the username (curator) next to the user icon in the Author column | Redirected to user profile page
 - Navigate to the right side and click on the disable user button in the 'User Profile' menu (below the normal top right hand menu). It looks like a circle with a line through it | A pop-up dialogue box appears asking 'Do you want to disable user?'
 - In the disable user pop-up dialogue box, select the OK button | User is disabled
 - Log out of Metadata Exchange | Logout successful
 - Login to Metadata Exchange as curator | 'Sorry your account is disabled' appears in the login dialogue box and curator cannot login
''')

class DisableUser extends GebSpec {

    def "login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("curator", "curator")
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
        createDataModelPage.name = "TEST_DATAMODEL_FOUR"
        createDataModelPage.description = "TEST_MODEL_DESCRIPTION"
        createDataModelPage.modelCatalogueId = "123abc"
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "logout as curator"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.clickUserDropdown()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.logout()
        then:
        at HomePage
    }

    def "login as suprevisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("supervisor", "supervisor")
        then:
        at DashboardPage
    }

    def "select created data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select("TEST_DATAMODEL_FOUR")
        then:
        at DataModelPage
    }

    def "select activity tab"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        true
    }
}
