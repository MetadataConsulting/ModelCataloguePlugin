package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1468')
@Title('During Creation of a new account, verify email field is not disabled when typing a wrong email address')
@Stepwise
class VerifyEmailFieldNotDisabledWhenOnWrongEmailAddressSpec extends GebSpec {

    @Shared
    String dataTypeName = UUID.randomUUID().toString()

    def "Signup as New user"() {
        when: 'login as a curator'
        SignUpPage signUpPage = to SignUpPage
        signUpPage.clickSignUp()
        then:
        at RegisterPage
    }

    def "Register new user and check email not disabled"() {
        when:
        RegisterPage registerPage = browser.page RegisterPage
        registerPage.register('NewUser','testuser@gma','testpassword','testpassword')

        then:
        !registerPage.isAlertDisplayed()
    }
}