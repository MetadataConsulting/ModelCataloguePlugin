package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import geb.spock.GebSpec
import org.modelcatalogue.repack.org.scribe.builder.api.FacebookApi
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 17/03/2017.
 */
class SearchMoreOptionPolicySpec extends GebSpec {
    private static final String create="a#role_data-models_create-data-modelBtn>span:nth-child(2)"
    private static final String name="input#name"
    private static final String policies="input#dataModelPolicy"
    private static final String searchMore="a.show-more-cep-item"
    private static final String  searchForData="input#value"
    void searchOption(){

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
        find(By.cssSelector(name)).value("My FirstTest")
        Thread.sleep(1000L)
        // enter policies
        $(By.cssSelector(policies)).value("my policies as a tester find bug")
        // select search more
        Thread.sleep(2000l)
        $(By.cssSelector(searchMore)).click()
        Thread.sleep(5000L)
        $(By.cssSelector(searchForData)).value("My new policy")
        Thread.sleep(1000L)
        driver.findElement(By.cssSelector(searchForData)).sendKeys(Keys.ENTER)

        then:
        noExceptionThrown()
        Thread.sleep(10000L)
        driver.close()




    }

}
