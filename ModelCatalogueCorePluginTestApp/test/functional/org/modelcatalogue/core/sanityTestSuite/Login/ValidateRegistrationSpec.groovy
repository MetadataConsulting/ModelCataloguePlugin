package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import spock.lang.Ignore

class ValidateRegistrationSpec extends  AbstractModelCatalogueGebSpec {
    private static final String newUsername="input#username-new"
    private static final String newEmail="input#email-new"
    private static final String  password="input#password"
    private static final String  password2="input#password2"
    public static final String  createButton ="button.btn"
    private static final String  header = "h3.panel-title"
    private static final String  alert  ="div.alert"
    static WebDriver driver

     @Ignore
    void goToRegistration() {

        when:'open browser and click on the SignUp button'
        driver = browser.driver
        go ('/register/')

        then:'verify the page title'
        check(header).contains("Create a new account")


        when:'enter new username and email'
        fill newUsername with("tatiana")
        fill(newEmail) with("berthe.kuatche@metadataconsulting.co.uk")

        and:'type password '
        fill(password) with("berthe32~~")
        fill(password2) with("berthe32~~")

        and:'click pn create'
        click createButton

        then:
        check(alert) displayed


    }

}
