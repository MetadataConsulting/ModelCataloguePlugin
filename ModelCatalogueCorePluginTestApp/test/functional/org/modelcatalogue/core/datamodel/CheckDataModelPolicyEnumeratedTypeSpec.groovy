package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import org.modelcatalogue.core.geb.*
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Stepwise

@Issue('https://metadata.atlassian.net/browse/MET-1766')
@Title('Check Model Policy - enumeratedType property')
@Narrative($/
 - Login to Metadata Exchange as supervisor or curator | Login successful
 - Navigate to the top right menu and click on the Settings menu button | Settings menu drop-down appears
 - Select Data Model Policies from Settings menu drop-down | Redirected to Data Model Policies page is displayed. 'Data Model Policies' is the title
 - From list of Data Model Policies, select 'Enumeration Checks' | Redirected to 'Enumeration Checks' policy main page. 'Enumeration Checks' is the title.
 - Check that the Enumeration Policy Text is correct . | Enumeration Checks Policy Text is the same as shown below:
//key-value should be lowercase and underscore separated and no special characters
check enumeratedType property 'enumAsString' apply negativeRegex: /.*"key"\s*:\s*(?!"[a-z0-9]+").*/
/$)

@Stepwise
class CheckDataModelPolicyEnumeratedTypeSpec extends GebSpec {

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage
    }

    def "open datamodel policies in settings dropdown"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.cogMenu()
        then:
        at DashboardPage

        when:
        dashboardPage = browser.page DashboardPage
        dashboardPage.nav.dataModelPolicies()
        then:
        at DataModelPolicyListPage

    }

    def "select enumeration check"() {
        when:
        DataModelPolicyListPage dataModelPolicyListPage = browser.page DataModelPolicyListPage
        dataModelPolicyListPage.selectEnumeratedTypePolicy()
        then:
        at DataModelPolicyEnumerationPage
    }

    def "check enumeration content"() {
        when:
        DataModelPolicyEnumerationPage dataModelPolicyPage = browser.page DataModelPolicyEnumerationPage
        then:
        assert "//key-value should be lowercase and underscore separated and no special characters \n" +
                "check enumeratedType property 'enumAsString' apply negativeRegex: /.\"key\"\\s*:\\s*(?!\"[a-z0-9]+\")./" == dataModelPolicyPage.policyText()
    }
}
