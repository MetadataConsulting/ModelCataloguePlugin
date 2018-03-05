package org.modelcatalogue.core.Regression

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataModelPolicyCreatePage
import org.modelcatalogue.core.geb.DataModelPolicyListPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
class CreateDataModelAndCreatePolicesSpec extends GebSpec {

    void "create a data model policy and use it in the creation of a data model"() {

        when: 'login to model catalogue'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        dashboardPage.nav.dataModelPolicies()

        then:
        at DataModelPolicyListPage

        when:
        DataModelPolicyListPage dataModelPolicyListPage = browser.page DataModelPolicyListPage
        int numberOfDataModelPolicyListPage = dataModelPolicyListPage.countDataModelPolicyLinks()

        dataModelPolicyListPage.create()
        DataModelPolicyCreatePage dataModelPolicyCreatePage = browser.page DataModelPolicyCreatePage
        dataModelPolicyCreatePage.name = 'TESTING_POLICY'
        dataModelPolicyCreatePage.policyText = "check dataElement property 'name' is 'unique'"
        dataModelPolicyCreatePage.save()
        dataModelPolicyListPage = browser.page DataModelPolicyListPage

        then:
        waitFor {(numberOfDataModelPolicyListPage + 1) == dataModelPolicyListPage.countDataModelPolicyLinks()}

        when:
        dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = "My First Test"
        createDataModelPage.semanticVersion = "0.0.1"
        createDataModelPage.modelCatalogueId = "MET-89765"
        createDataModelPage.check('TESTING_POLICY')
        createDataModelPage.submit()

        then:
        at DataModelPage
    }
}
