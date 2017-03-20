package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 14/03/2017.
 */
class LoginAsViewerSpec extends GebSpec {

    void doLoginAsViewer(){
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


    }
}
