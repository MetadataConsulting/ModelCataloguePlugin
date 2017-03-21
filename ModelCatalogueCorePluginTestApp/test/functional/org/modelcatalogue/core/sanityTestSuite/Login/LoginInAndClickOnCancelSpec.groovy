package org.modelcatalogue.core.sanityTestSuite.Login


import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 13/03/2017.
 */
class LoginInAndClickOnCancelSpec extends AbstractModelCatalogueGebSpec {

    private static final String cancel = "button.btn-warning"
    private static final String username="input#username"
    private static final String password="input#password"
    private static final String login="button.btn"
    private static final String primaryBtn="button.btn"
    void clickOnCancel() {
        when:
            WebDriver driver = browser.driver
            go(baseUrl)
              click login
        then:
            $("a.btn-block").text()=="Login with Google"

       when:
            // enter the username
            fill(username)with("viewer")
            // enter password
            fill(password)with("viewer")
            // click on cancel
            click cancel

        then:
             check(primaryBtn)is("Login")


    }

}
