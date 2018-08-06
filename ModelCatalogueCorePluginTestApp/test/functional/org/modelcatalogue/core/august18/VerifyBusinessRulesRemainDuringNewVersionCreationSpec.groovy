package org.modelcatalogue.core.august18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1487')
@Title(' Verify Business rules remain during new version creation')
@Stepwise
class VerifyBusinessRulesRemainDuringNewVersionCreationSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String businessRule = UUID.randomUUID().toString()
    @Shared
    String businessName = UUID.randomUUID().toString()
    @Shared
    String businessComponent = UUID.randomUUID().toString()
    @Shared
    String version = "1.1.1"
    @Shared
    String versionNote = "version note"

    def "login as supervisor"() {
        when: 'login as a supervisor'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "create data model and business"() {
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

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.businessRules()
        then:
        at BusinessRulesPage

        when:
        BusinessRulesPage businessRulesPage = browser.page BusinessRulesPage
        businessRulesPage.addBusinessRuleClick()
        then:
        at CreateBusninessRulesPages

        when:
        CreateBusninessRulesPages createBusninessRulesPages = browser.page CreateBusninessRulesPages
        createBusninessRulesPages.name = businessName
        createBusninessRulesPages.component = businessComponent
        createBusninessRulesPages.rule = businessRule
        createBusninessRulesPages.submit()
        then:
        at BusinessRulesPage

        when:
        businessRulesPage = browser.page BusinessRulesPage
        businessRulesPage.treeView.dataModel()
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

    def "verify business rule populated"() {
        when:
        BusinessRulesPage businessRulesPage = browser.page BusinessRulesPage
        then:
        businessRulesPage.isBusinessElementVisible(businessName)

        when:
        businessRulesPage = browser.page BusinessRulesPage
        businessRulesPage.treeView.dataModel()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dataModel()
        dataModelPage.dropdownMenu.createNewVersion()
        then:
        at DataModelAssignNewVersionPage

        when:
        DataModelAssignNewVersionPage dataModelAssignNewVersionPage = browser.page DataModelAssignNewVersionPage
        dataModelAssignNewVersionPage.semanticVersion = "0.0.2"
        dataModelAssignNewVersionPage.createNewVersion()
        then:
        at DataModelAssignNewVersionConfirmPage

        when:
        DataModelAssignNewVersionConfirmPage dataModelAssignNewVersionConfirmPage = browser.page DataModelAssignNewVersionConfirmPage
        dataModelAssignNewVersionConfirmPage.hide()
        then:
        at DataModelPage
    }

    def "verify business rule populated in new version"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.businessRules()
        then:
        at BusinessRulesPage

        when:
        BusinessRulesPage businessRulesPage = browser.page BusinessRulesPage
        then:
        businessRulesPage.isBusinessElementVisible(businessName)
    }
}