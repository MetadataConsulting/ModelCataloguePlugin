package org.modelcatalogue.core.sanityTestSuite

import geb.spock.GebSpec
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 13/03/2017.
 */
class LoginSpec extends GebSpec {

    void doLoginAndClickCheckBox (){

        WebDriver driver = browser.driver
        go("https://gel-mc-test.metadata.org.uk/#/")
        // click on login
        $("button.btn").click()
        then:
        // verify that username or email present on the page
       assert $("div.modal-body>form>div:nth-child(1)>label").text()=="Username or Email"

        when:
        // enter username , password and check remenber me
        $("input#username").value("")
        $("input#password").value("")
        $("div.checkbox>label>input").click()
        // click on login
        $("button.btn-success").click()

        then:
        // verify the title
        assert $("span.mc-name").text()=="Model Catalogue"

    }
}
