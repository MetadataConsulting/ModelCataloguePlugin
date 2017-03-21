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
    private static final String password=""
    private static final String login="button.btn"
    void clickOnCancel() {
        when:
        WebDriver driver = browser.driver
        go(baseUrl)
        click login

        // click on cancel
        Thread.sleep(1000l)
       click cancel

        then:
        $("button",class:"btn btn-large btn-primary").text()== "Login"

    }

}
