package org.modelcatalogue.core.RegressionTestSuite

import geb.spock.GebSpec
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 14/03/2017.
 */
class VerifyResetPasswordPresentOnLoginPageSpec extends GebSpec {

    def"verify reset password link present"(){

        when:
        // navigate to model catalogue and click on Login button
        WebDriver driver = browser.driver
        go("https://gel-mc-test.metadata.org.uk/#/")
        $("button.btn").click()


        thedn:
        // verify reset password links present by enter the locator or manually observe the present of reset Password
        assert $("").text()=="Reset Password"

    }
}
