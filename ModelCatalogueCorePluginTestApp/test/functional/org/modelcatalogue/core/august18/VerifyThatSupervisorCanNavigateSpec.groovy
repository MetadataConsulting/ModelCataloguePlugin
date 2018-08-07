package org.modelcatalogue.core.august18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1485')
@Title('Verify that Supervisor can navigate')
@Stepwise
class VerifyThatSupervisorCanNavigateSpec extends GebSpec {

    def "login as supervisor"() {
        when: 'login as a supervisor'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "select setting button and verify each page"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.dataModelPermission()
        then:
        at DataModelAclPermissionsPage

        when:
        DataModelAclPermissionsPage dataModelAclPermissionsPage = browser.page DataModelAclPermissionsPage
        dataModelAclPermissionsPage.nav.cogMenu()
        dataModelAclPermissionsPage.nav.codeversion()
        then:
        at CodeVersionPage

        when:
        CodeVersionPage codeVersionPage = browser.page CodeVersionPage
        codeVersionPage.nav.cogMenu()
        codeVersionPage.nav.mappingUtility()
        then:
        at MappingPage

        when:
        MappingPage mappingPage = browser.page MappingPage
        mappingPage.nav.cogMenu()
        mappingPage.nav.activity()
        then:
        at LastSeenPage

        when:
        LastSeenPage lastSeenPage = browser.page LastSeenPage
        lastSeenPage.nav.cogMenu()
        lastSeenPage.nav.reindexCatalogue()
        then:
        at ReindexCataloguePage

        when:
        ReindexCataloguePage reindexCataloguePage = browser.page ReindexCataloguePage
        reindexCataloguePage.nav.cogMenu()
        reindexCataloguePage.nav.relationshipTypes()
        then:
        at RelationshipTypesPage

        when:
        RelationshipTypesPage relationshipTypesPage = browser.page RelationshipTypesPage
        relationshipTypesPage.nav.adminMenu()
        relationshipTypesPage.nav.dataModelPolicies()
        then:
        at DataModelPolicyListPage

        when:
        DataModelPolicyListPage dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.nav.settingDropDown()
        dataModelPolicyListPage.nav.logs()
        ArrayList<String> tabs2 = driver.getWindowHandles()
        driver.switchTo().window(tabs2.get(1))
        then:
        at LogsPage

        when:
        driver.switchTo().window(tabs2.get(0))
        dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.nav.settingDropDown()
        dataModelPolicyListPage.nav.feedbacks()
        tabs2 = driver.getWindowHandles()
        driver.switchTo().window(tabs2.get(2))
        println("tabs" + tabs2.size())
        then:
        at FeedbackPage

        when:
        dashboardPage = to DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.logout()
        then:
        at HomePage
    }
}