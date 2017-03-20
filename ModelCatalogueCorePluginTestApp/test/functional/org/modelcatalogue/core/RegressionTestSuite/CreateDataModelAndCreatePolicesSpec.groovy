package org.modelcatalogue.core.RegressionTestSuite

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 17/03/2017.
 */
class CreateDataModelAndCreatePolicesSpec extends GebSpec {
    private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name="input#name"
    private static final String version="input#semanticVersion"
    private static final String catalogueId="input#modelCatalogueId"
    private static final String policies="input#dataModelPolicy"
    private static final String moreIcon="span.search-for-more-icon"
    private static final String uniqueOfKind ="div.list-group>a:nth-child(2)"
    private static final String createNew="a.create-new-cep-item"
    private static final String nameP="div.basic-edit-modal-prompt>div>div>div:nth-child(2)>form>div:nth-child(1)>input"
    private static final String policyText="textarea#policyText"
    private static final String save ="a#role_modal_modal-save-elementBtn"


    void addPolicies(){
        when:
        WebDriver driver = browser.driver
        go(baseUrl)
        // click on login
        $("button.btn").click()
        then:
        // verify that username or email present on the page
        assert $("label",for:"username").text()=="Username or Email"

        when:
        // enter username , password and check remember me
        $("input#username").value("viewer")
        $("input#password").value("viewer")
        $("div.checkbox>label>input").click()
        // click on login
        $("button.btn-success").click()
        Thread.sleep(1000L)

        then:
        noExceptionThrown()

        when:
        // click on create
        find(By.cssSelector(create)).click()
        // wait for one min
        Thread.sleep(1000L)
        // type a name . please change value
        find(By.cssSelector(name)).value("My First Test3")
        Thread.sleep(1000L)
        // type a version
        find(By.cssSelector(version)).value("2.1.29")
        Thread.sleep(1000L)
        // type a id
        find(By.cssSelector(catalogueId)).value("33")
        Thread.sleep(1000L)
        //click om more icon
        $(By.cssSelector(moreIcon)).click()
        Thread.sleep(1000L)
        // select unique of kind
        driver.findElement(By.cssSelector(uniqueOfKind)).click()
        //$(By.cssSelector(uniqueOfKind)).click()
        Thread.sleep(1000L)
        // enter policies
        $(By.cssSelector(policies)).value("my policies as a tester find bug")
        Thread.sleep(1000L)
        // Select create new
        find(By.cssSelector(createNew)).click()
        Thread.sleep(1000L)
        // type policy name
        find(By.cssSelector(nameP)).value("My Test")
        // type policy
        find(By.cssSelector(policyText)).value("Testing is fun")
        //Thread.sleep(1000L)
        // CLICK ON SAVE
        $(By.cssSelector(save)).click()

        then:
        noExceptionThrown()
        Thread.sleep(1000L)
        driver.close()

    }
}
