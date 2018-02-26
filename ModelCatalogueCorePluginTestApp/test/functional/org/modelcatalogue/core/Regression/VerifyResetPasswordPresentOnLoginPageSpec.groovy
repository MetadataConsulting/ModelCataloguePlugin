package org.modelcatalogue.core.Regression

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.HomePage
import org.openqa.selenium.WebDriver
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env')  })
class VerifyResetPasswordPresentOnLoginPageSpec extends GebSpec {

    def "verify reset password link present"() {
        when:
        to HomePage

        then:
        at HomePage

        when:
        HomePage homePage = browser.page HomePage
        homePage.login()

        then:
        noExceptionThrown()
    }
}
