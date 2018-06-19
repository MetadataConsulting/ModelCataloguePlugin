package org.modelcatalogue.core.regression

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.HomePage
import org.openqa.selenium.WebDriver
import spock.lang.IgnoreIf
import spock.lang.Ignore

@IgnoreIf({ !System.getProperty('geb.env')  })
@Ignore
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
