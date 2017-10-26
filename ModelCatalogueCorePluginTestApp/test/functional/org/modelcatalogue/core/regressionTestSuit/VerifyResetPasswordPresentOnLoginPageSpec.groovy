package org.modelcatalogue.core.regressionTestSuit

import geb.spock.GebSpec
import org.openqa.selenium.WebDriver


class VerifyResetPasswordPresentOnLoginPageSpec extends GebSpec {

    def"verify reset password link present"(){

        when:
        // navigate to model catalogue and click on Login button
        WebDriver driver = browser.driver
        go(baseUrl)
        $("button.btn").click()


        then:
        noExceptionThrown()

    }
}
