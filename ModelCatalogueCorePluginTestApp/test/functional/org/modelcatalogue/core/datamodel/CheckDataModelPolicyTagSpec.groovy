package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.*

@Issue('https://metadata.atlassian.net/browse/MET-1526')
@Title('Examine Data Model Policy Tag')
@Narrative('''
 - Login to Metadata Exchange As admin | Login successful
 - Select 'Create New Data Model' button (plus sign) from the top-right hand menu | Redirected to 'Create Data Model' page
 - Fill the form: Name, Semantic Version, Catalogue ID
 - From the check list of Policies select Unique of Kind data policy | Unique of kind data policy is selected
 - Click on the green Save button | Data model is created. Redirected to new Data Model main page
 - In the display panel on the right hand side under the title and under policies select the 'Unique of Kind' link | Redirected to Unique of Kind policy page
 - Verify that within the Policy text there is no version numbers and each property name is classed as 'Unique' | No version numbers and all properties unique
 - On the data model page, navigate within the display panel to select the 'edit' button on the right hand side of the data model name | Data Model information is now editable
 - In the display panel, Under the policies section, select the 'X' (delete) button next to the 'Unique of Kind' policy to delete the policy from the data model | Data Model Policy is deleted
 - Select the 'tick' Save button to finish editing the data model | Data Model information is no longer editable
 - Verify that underneath the policies section for the data model, there are no data model policies. | Verify that 'Unique of Kind' data model policy has been deleted
''')
@Stepwise
class CheckDataModelPolicyTagSpec extends GebSpec {

    def "Login as admin"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login("supervisor", "supervisor")
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
        createDataModelPage.name = "TESTING_MODEL_ON"
        createDataModelPage.description = "TESTING_MODEL_DESCRIPTION"
        createDataModelPage.modelCatalogueIdInput = "KDJFKD9349"
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "verify policy text has no version number"() {
        given:
        // Store the current window handle
        String winHandleBefore = driver.getWindowHandle();

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.selectUniqueOfKindPolicy()
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
        then:
        at UniqueOfKindPolicyPage

        when:
        UniqueOfKindPolicyPage uniqueOfKindPolicyPage = browser.page UniqueOfKindPolicyPage
        then:
        uniqueOfKindPolicyPage.allPropertiesAreUnique()

        when:
        driver.close();
        driver.switchTo().window(winHandleBefore);
        then:
        at DataModelPage
    }

    def "edit data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.editModel()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.removeUniqueOfKindPolicy()
        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.save()
        then:
        at DataModelPage
    }

    def "verify unique of a kind policy is gone"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        !dataModelPage.UniqueOfKindPolicyAdded()
    }

}
