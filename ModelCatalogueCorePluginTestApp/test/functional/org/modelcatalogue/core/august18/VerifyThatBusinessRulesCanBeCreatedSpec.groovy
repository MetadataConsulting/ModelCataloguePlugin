package org.modelcatalogue.core.august18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1488')
@Title('Verify that Business Rules can be created')
@Stepwise
class VerifyThatBusinessRulesCanBeCreatedSpec extends GebSpec {


    @Shared
    String businessRule = UUID.randomUUID().toString()
    @Shared
    String businessName = UUID.randomUUID().toString()
    @Shared
    String businessComponent = UUID.randomUUID().toString()


    def "login as supervisor"() {
        when: 'login as a supervisor'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "create data model and business"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search("Test 1")
        dashboardPage.select("Test 1")
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
        then:
        businessRulesPage.isBusinessElementVisible(businessName)
    }
}