package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.RegisterPage
import org.openqa.selenium.WebDriver

class ValidateRegistrationSpec extends GebSpec {

    void goToRegistration() {

        when:'Password must have at least one letter, number, and special character and should be longer then 8 characters.'
        to RegisterPage

        then:
        at RegisterPage

        when:
        RegisterPage registerPage = browser.page RegisterPage
        registerPage.register("tatiana", "berthe.kuatche@metadataconsulting.co.uk", "berthe32~~", "berthe32~~")

        then:
        registerPage.isAlertDisplayed()
    }
}
