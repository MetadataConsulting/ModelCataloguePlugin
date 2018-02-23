package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.HomePage
import org.openqa.selenium.WebDriver
import spock.lang.Ignore
import spock.lang.IgnoreIf

//@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
class ResetPasswordSpec extends AbstractModelCatalogueGebSpec {

    private static final String resetPassword ="p.help-block>a"
    private static final String email ="input#username-new"
    private static final String resetMyPassword ="button.btn"

    @Ignore
    void doResetPassword() {
        when:
        to HomePage

        then:
        at HomePage

        when:
        HomePage homePage = browser.page HomePage
        homePage.login()

        then:'verify that username or email present on the page'
        $("label", for: "username").text() == "Username or Email"

        when:'click on reset password button'
        click resetPassword

        then:'verify header'
        $("div.panel-heading").text() == "Forgot Password"

        when:'enter email address and click on the reset password'
        fill(email) with("berthe.kuatche@metadataconsulting.co.uk")
        click resetMyPassword


        then:'verify the error message'
        $("div.alert-danger").text() == "No user was found with that username"


        when:'re- type the username'
        fill(email) with("viewer")

        and:'click rest my password button'
        click resetMyPassword

        then:'verify the second error message'
        $("div.alert-danger").text() == "Given user doesn't have any email associated. Please, contact the administrator."
    }
}
