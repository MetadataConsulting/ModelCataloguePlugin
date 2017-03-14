package org.modelcatalogue.core.sanityTestSuite

import geb.spock.GebSpec
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 14/03/2017.
 */
class ResetPasswordSpec extends GebSpec {

    void doResetPassword(){
        when:
        // navigate to model catalogue
        WebDriver driver = browser.driver
        go("https://gel-mc-test.metadata.org.uk/#/")
        // click on login
        $("button.btn").click()
        then:
        // verify that username or email present on the page
        assert $("div.modal-body>form>div:nth-child(1)>label").text()=="Username or Email"



    }



}
