package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 13/03/2017.
 */
class LoginSpec extends GebSpec {

    void doLoginAndClickCheckBox (){
       when:
        WebDriver driver = browser.driver
        go(baseUrl)
        // click on login
        $("button.btn").click()
        then:
        // verify that username or email present on the page
       assert $("label",for:"username").text()=="Username or Email"

        when:
        // enter username , password and check remenber me
        $("input#username").value("viewer")
        $("input#password").value("viewer")
        $("div.checkbox>label>input").click()
        // click on login
        $("button.btn-success").click()
        Thread.sleep(1000L)

        then:

      // assert $("//a[contains(text(),'My Models')]").text() ==" My Models"
        noExceptionThrown()
        driver.close()

    }
}
