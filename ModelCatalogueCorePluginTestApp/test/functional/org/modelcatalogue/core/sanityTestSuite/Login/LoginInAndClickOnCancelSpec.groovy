package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.WebDriver
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
class LoginInAndClickOnCancelSpec extends AbstractModelCatalogueGebSpec {

    private static final String cancel = "button.btn-warning"
    private static final String username = "input#username"
    private static final String password = "input#password"
    private static final String login = "button.btn"
    private static final String primaryBtn = "button.btn"

    void clickOnCancel() {
        when:
        go(baseUrl)
        click login
        then:
        $("a.btn-block").text() == "Login with Google"

        when:'enter the username and password'
        fill(username) with("viewer")
        fill(password) with("viewer")

        and:'click on cancel'
        click cancel

        then:
        check(primaryBtn) is("Login")


    }

}
