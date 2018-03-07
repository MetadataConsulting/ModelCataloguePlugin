package org.modelcatalogue.core.sanityTestSuite.LandingPage

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
class NavItemVisibilitySpec extends GebSpec {

    def "check navigation item visibility for viewers"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('user', 'user')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage

        then:
        dashboardPage.nav.homeLink.isDisplayed()

        and:
        !dashboardPage.nav.createDataModelLink.isDisplayed()

        and:
        !dashboardPage.nav.importMenuLink.isDisplayed()
        !dashboardPage.nav.importExcelLink.isDisplayed()
        !dashboardPage.nav.importOboLink.isDisplayed()
        !dashboardPage.nav.importDslLink.isDisplayed()
        !dashboardPage.nav.importXmlLink.isDisplayed()

        and:
        dashboardPage.nav.userMenuLink.isDisplayed()

        when:
        dashboardPage.nav.userMenu()

        then:
        dashboardPage.nav.usernameLink.isDisplayed()
        dashboardPage.nav.favouriteLink.isDisplayed()
        dashboardPage.nav.apiKeyLink.isDisplayed()
        dashboardPage.nav.logoutLink.isDisplayed()

        and:
        dashboardPage.nav.cogMenuLink.isDisplayed()

        when:
        dashboardPage.nav.cogMenu()

        then:
        dashboardPage.nav.codeversionLink.isDisplayed()
        dashboardPage.nav.relationshipTypesLink.isDisplayed()
        dashboardPage.nav.dataModelPolicyLink.isDisplayed()
        dashboardPage.nav.feedbacksLink.isDisplayed()

        and:
        !dashboardPage.nav.dataModelPermissionLink.isDisplayed()
        !dashboardPage.nav.usersLink.isDisplayed()
        !dashboardPage.nav.mappingUtilityLink.isDisplayed()
        !dashboardPage.nav.activityLink.isDisplayed()
        !dashboardPage.nav.reindexCatalogueLink.isDisplayed()
        !dashboardPage.nav.monitoringLink.isDisplayed()
        !dashboardPage.nav.logsLink.isDisplayed()
    }


    def "check navigation item visibility for supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage

        then:
        dashboardPage.nav.homeLink.isDisplayed()

        and:
        dashboardPage.nav.createDataModelLink.isDisplayed()

        and:
        dashboardPage.nav.importMenuLink.isDisplayed()

        when:
        dashboardPage.nav.importMenu()

        then:
        dashboardPage.nav.importMenuLink.isDisplayed()
        dashboardPage.nav.importExcelLink.isDisplayed()
        dashboardPage.nav.importOboLink.isDisplayed()
        dashboardPage.nav.importDslLink.isDisplayed()
        dashboardPage.nav.importXmlLink.isDisplayed()

        and:
        dashboardPage.nav.userMenuLink.isDisplayed()

        when:
        dashboardPage.nav.userMenu()

        then:
        dashboardPage.nav.usernameLink.isDisplayed()
        dashboardPage.nav.favouriteLink.isDisplayed()
        dashboardPage.nav.apiKeyLink.isDisplayed()
        dashboardPage.nav.logoutLink.isDisplayed()

        and:
        dashboardPage.nav.cogMenuLink.isDisplayed()

        when:
        dashboardPage.nav.cogMenu()

        then:
        dashboardPage.nav.codeversionLink.isDisplayed()
        dashboardPage.nav.relationshipTypesLink.isDisplayed()
        dashboardPage.nav.dataModelPolicyLink.isDisplayed()
        dashboardPage.nav.feedbacksLink.isDisplayed()
        dashboardPage.nav.dataModelPermissionLink.isDisplayed()
        dashboardPage.nav.usersLink.isDisplayed()
        dashboardPage.nav.mappingUtilityLink.isDisplayed()
        dashboardPage.nav.activityLink.isDisplayed()
        dashboardPage.nav.reindexCatalogueLink.isDisplayed()
        dashboardPage.nav.monitoringLink.isDisplayed()
        dashboardPage.nav.logsLink.isDisplayed()
    }


    def "check navigation item visibility for curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage

        then:
        dashboardPage.nav.homeLink.isDisplayed()

        and:
        dashboardPage.nav.createDataModelLink.isDisplayed()

        and:
        dashboardPage.nav.importMenuLink.isDisplayed()

        when:
        dashboardPage.nav.importMenu()

        then:
        dashboardPage.nav.importMenuLink.isDisplayed()
        dashboardPage.nav.importExcelLink.isDisplayed()
        dashboardPage.nav.importOboLink.isDisplayed()
        dashboardPage.nav.importDslLink.isDisplayed()
        dashboardPage.nav.importXmlLink.isDisplayed()

        and:
        dashboardPage.nav.userMenuLink.isDisplayed()

        when:
        dashboardPage.nav.userMenu()

        then:
        dashboardPage.nav.usernameLink.isDisplayed()
        dashboardPage.nav.favouriteLink.isDisplayed()
        dashboardPage.nav.apiKeyLink.isDisplayed()
        dashboardPage.nav.logoutLink.isDisplayed()

        and:
        dashboardPage.nav.cogMenuLink.isDisplayed()

        when:
        dashboardPage.nav.cogMenu()

        then:
        dashboardPage.nav.codeversionLink.isDisplayed()
        dashboardPage.nav.relationshipTypesLink.isDisplayed()
        dashboardPage.nav.dataModelPolicyLink.isDisplayed()
        dashboardPage.nav.feedbacksLink.isDisplayed()
        dashboardPage.nav.mappingUtilityLink.isDisplayed()

        and:
        !dashboardPage.nav.dataModelPermissionLink.isDisplayed()
        !dashboardPage.nav.usersLink.isDisplayed()
        !dashboardPage.nav.activityLink.isDisplayed()
        !dashboardPage.nav.reindexCatalogueLink.isDisplayed()
        !dashboardPage.nav.monitoringLink.isDisplayed()
        !dashboardPage.nav.logsLink.isDisplayed()
    }
}
