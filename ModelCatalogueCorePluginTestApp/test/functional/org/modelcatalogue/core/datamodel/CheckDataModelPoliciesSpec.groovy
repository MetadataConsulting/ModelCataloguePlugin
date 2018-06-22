package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Ignore
import spock.lang.Title
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.*
import spock.lang.Shared

@Issue('https://metadata.atlassian.net/browse/MET-1604')
@Title('Data Model is created with selected policies')
@Narrative('''
 - Login to Metadata Exchange As curator | Login successful
 - Click on the 'Create Data Model' button (plus sign) from top-right hand menu | Redirected to 'Create Data Model' page
 - Fill the form with Name, Catalogue ID, Description and select from multiple choice list of data policies either Unique of Kind or Default Checks. Click Save button | Data Model is created. Redirected to new data model main page
 - Examine that in the Display panel on the right side, that underneath the Data Model name, in the Policies section, the data model policy that was chosen is present. | Confirm that the Data Model has been created with the Data Model Policies
''')
@Stepwise
class CheckDataModelPoliciesSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("curator", "curator")
        then:
        at DashboardPage
    }

    def "create a data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.description = dataModelDescription
        createDataModelPage.modelCatalogueIdInput = UUID.randomUUID().toString()
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "check data model has uniqueofkind policy"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        !dataModelPage.defaultChecksPolicyAdded()
        dataModelPage.UniqueOfKindPolicyAdded()
    }

}
