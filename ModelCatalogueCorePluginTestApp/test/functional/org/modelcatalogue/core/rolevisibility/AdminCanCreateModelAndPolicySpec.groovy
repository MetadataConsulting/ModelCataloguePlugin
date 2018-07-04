package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Ignore
import spock.lang.Stepwise
import spock.lang.Shared
import org.modelcatalogue.core.geb.*

@Issue('https://metadata.atlassian.net/browse/MET-1443')
@Title('Test that Admin user can create a new policy when creating a data model')
@Narrative($/
 - Login to Metadata Exchange As supervisor | Login successful
 - Click on Create Data Model button (plus sign) in top right hand menu | Redirected to Create Data Model page
 - Fill form with Name, Catalogue ID, Description
 - From Policies list select one policy from either Default Checks or Unique of Kind | Checkbox is selected
 - Click on the save button at the bottom of the page | Data Model is created. Redirected to main page of new data model.
 - On display panel, check that in the Policies section the selected policy is shown | Selected policy appears in Policies section
 - To the right hand side menu (right of the Data Model Name), select the edit button. | Display panel is now editable. Search bar appears in policy section
 - In the search bar within the Policy section of the display panel, type a new name for a Policy (that doesn't yet exist) | Drop down appears from search bar with option to 'Create New'
 - Select the 'Create New' option from the drop down that appears under the search bar | 'Create Data Policy' pop-up dialogue box appears
 - Fill form with Name and set Policy Text to: "check dataElement property 'name' is 'unique'". Press the Save button | New Data Policy is created
 - Verify that in the display panel, the new data policy is listed under the Policies section of the Data Model | The new policy is listed (as well as the initial selected policy)
 - Click on the save edits (tick sign) button in the right hand menu of the data model display panel to close the edit view. | Edit view of display panel is closed
 - Select the new data policy. | Redirected to individual data policy page
 - Verify that name and policy text are correct. | Name and policy text are correct
/$)
@Stepwise
class AdminCanCreateModelAndPolicySpec extends GebSpec {

    @Shared
    String datamodelName = UUID.randomUUID().toString()
    @Shared
    String datamodelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    List<String> policies = []
    @Shared
    String newPolicyName = "New Policy"
    @Shared
    String newPolicyText = "check dataElement property 'name' is 'unique'"

    def "login as supervisor"() {
        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "create data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = datamodelName
        createDataModelPage.modelCatalogueIdInput = UUID.randomUUID().toString()
        createDataModelPage.description = datamodelDescription
        println(policies = createDataModelPage.selectedPolicyName())
        createDataModelPage.submit()
        then:
        at DataModelPage
    }


    def "selected data model policy is shown"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        dataModelPage.containsPolicies(policies)
    }

    def "edit the data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.editDataModel()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.searchPolicy(newPolicyName)
        dataModelPage.selectCreateNew()
        then:
        at DataModelPolicyCreatePage
    }

    def "create new data model policy"() {
        when:
        DataModelPolicyCreatePage dataModelPolicyCreatePage = browser.page DataModelPolicyCreatePage
        dataModelPolicyCreatePage.name = newPolicyName
        dataModelPolicyCreatePage.policyText = newPolicyText
        dataModelPolicyCreatePage.save()
        then:
        at DataModelPage
    }


    def "verify new policy is added"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        dataModelPage.policyAdded(newPolicyName)
    }

    def "save data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.saveModel()
        then:
        at DataModelPage
    }

    def "select policy"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.selectPolicy(newPolicyName)
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
        then:
        at DataModelPolicyPage
    }

    def "compare the policy text"() {
        when:
        DataModelPolicyPage dataModelPolicyPage = browser.page DataModelPolicyPage
        then:
        dataModelPolicyPage.policyTextIs(newPolicyText)
        dataModelPolicyPage.titleIs(newPolicyName)
    }

}