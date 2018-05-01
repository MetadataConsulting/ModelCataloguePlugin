package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.*

@Issue('https://metadata.atlassian.net/browse/MET-1485')
@Title('Verify that user ( role Supervisor) can click on settings menu button and navigate')
@Narrative('''
 - Login to Metadata Exchange As Supervisor | Login successful
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Scroll down and click on the Data Model ACL option | Directed to Data Model ACL page ( title is 'Data Model Permissions')
 - On the top right hand menu, select the Settings menu button | Settings menu drop-down appears
 - Select 'Code version' option from the drop down menu | Redirected to Code version page. Code version is displayed.
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Mapping Utility' option from the drop down menu | Redirected to Mapping Utility page. 'Mapping Batches is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Activity' option from the drop down menu | Redirected to User Activity page. 'User Activity' is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Reindex Catalogue' option from the drop down menu | Redirected to Reindex Catalogue page. 'Reindex Catalogue' is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Relationship Types' option from the drop down menu | Redirected to Relationship Types page. 'Relationship Types' is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Data Model Policies' option from the drop down menu | Redirected to Data Model Policies page. 'Data Model Policies' is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Monitoring' option from the drop down menu | A new tab in browser should open onto the Monitoring page.
 - Navigate back to page with Metadata Exchange
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Logs' option from the drop down menu | A new tab in browser should open onto the Logs page ( still within the Metadata Exchange). Title should display as 'Logs'.
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Feedbacks' option from the drop down menu | Redirected to Feedbacks page. 'Feedbacks' is displayed as title
''')
@Stepwise
class VerifySupervisorCanActionSettingsSpec extends GebSpec {

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("supervisor", "supervisor")
        then:
        at DashboardPage
    }

    def "navigate to DataModelAcl"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        then:
        at DashboardPage

        when:
        dashboardPage = browser.page DashboardPage
        dashboardPage.nav.dataModelPermission()
        then:
        at DataModelAclPermissionsPage
    }

    def "navigate to codeversion"() {
        when:
        DataModelAclPermissionsPage dataModelAclPermissionsPage = browser.page DataModelAclPermissionsPage
        dataModelAclPermissionsPage.nav.cogMenu()
        then:
        at DataModelAclPermissionsPage

        when:
        dataModelAclPermissionsPage = browser.page DataModelAclPermissionsPage
        dataModelAclPermissionsPage.nav.codeversion()
        then:
        at CodeVersionPage
    }

    /*def "navigate to mappingutility"() {
        when:
        CodeVersionPage codeVersionPage = browser.page CodeVersionPage
        codeVersionPage.nav.cogMenu()
        then:
        at CodeVersionPage

        when:
        codeVersionPage = browser.page CodeVersionPage
        codeVersionPage.nav.mappingUtility()
        then:
        at MappingUtilityPage
    }*/

    def "navigate to useractivity"() {
        when:
        CodeVersionPage codeVersionPage = browser.page CodeVersionPage
        codeVersionPage.nav.cogMenu()
        then:
        at CodeVersionPage

        when:
        codeVersionPage = browser.page CodeVersionPage
        codeVersionPage.nav.activity()
        then:
        at LastSeenPage
    }

    def "navigate to reindex catalogue"() {
        when:
        LastSeenPage lastSeenPage = browser.page LastSeenPage
        lastSeenPage.nav.cogMenu()
        then:
        at LastSeenPage

        when:
        lastSeenPage = browser.page LastSeenPage
        lastSeenPage.nav.reindexCatalogue()
        then:
        at ReindexCataloguePage
    }

    def "navigate to relationshiptypes"() {
        when:
        ReindexCataloguePage reindexCataloguePage = browser.page ReindexCataloguePage
        reindexCataloguePage.nav.cogMenu()
        then:
        at ReindexCataloguePage

        when:
        reindexCataloguePage = browser.page ReindexCataloguePage
        reindexCataloguePage.nav.relationshipTypes()
        then:
        at RelationshipTypesPage
    }

    def "navigate to datamodelpolicy"() {
        when:
        RelationshipTypesPage relationshipTypesPage = browser.page RelationshipTypesPage
        relationshipTypesPage.nav.cogMenu()
        then:
        at RelationshipTypesPage //this page nav is with different name

        when:
        relationshipTypesPage = browser.page RelationshipTypesPage
        relationshipTypesPage.nav.dataModelPolicies()
        then:
        at DataModelPolicyListPage
    }

    def "navigate to monitoring"() {
        when:
        DataModelPolicyListPage dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.nav.cogMenu()
        then:
        at DataModelPolicyListPage

        when:
        dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.nav.monitoring()
        then:
        true
        //new browser window should open
    }

    def "navigate to logs"() {
        when:
        DataModelPolicyListPage dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.nav.cogMenu()
        then:
        at DataModelPolicyListPage

        when:
        dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.nav.logs()
        then:
        at LogsPage
        //new browser window should open
    }

    def "navigate to feedback"() {
        when:
        DataModelPolicyListPage dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.nav.cogMenu()
        then:
        at DataModelPolicyListPage

        when:
        dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.nav.feedbacks()
        then:
        at FeedbackPage
    }
}
