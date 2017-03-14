<<<<<<< Updated upstream
package org.modelcatalogue.core.sanityTestSuite
=======
package org.modelcatalogue.core.RegressionTestSuite
>>>>>>> Stashed changes

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 13/03/2017.
 */
class InvalidRegistrationSpec extends GebSpec {
    public static final String model = "div.panel-body>div"
    static WebDriver driver

    void invalidRegistration() {
<<<<<<< Updated upstream
=======
        // navigate to Model Catalogue
>>>>>>> Stashed changes
        when:
        driver = browser.driver
        driver.manage().deleteAllCookies()
        go("https://gel-mc-test.metadata.org.uk/#/")
<<<<<<< Updated upstream
        $("a.btn").click()
        then:
        assert $("span", class: "mc-name").text() == "Model Catalogue"

        when:
        $("input#username-new").value("tatiana")
        find(By.cssSelector("input#email-new")).value("berthe.kuatche@metadataconsulting")
        $("input#password").value("berthe32~~")
        $("input#password2").value("berthe32~~")
        $("button.btn").click()

        then:
=======
        // click on Sign Up
        $("a.btn").click()
        then:
        // verify the title
        assert $("span", class: "mc-name").text() == "Model Catalogue"

        when:
        // enter username
        $("input#username-new").value("tatiana")
        // enter wrong email address
        find(By.cssSelector("input#email-new")).value("berthe.kuatche@metadataconsulting")
        // TYPE PASSWORD
        $("input#password").value("berthe32~~")
        $("input#password2").value("berthe32~~")
        // CLICK ON CREATE
        $("button.btn").click()
        // WAIT FOR 1 MIN
         Thread.sleep(1000L)
        then:
        // VERIFY THIS TEXT PRESENT
>>>>>>> Stashed changes
        assert $("div.panel-body>div:nth-child(1)").text()== "Please provide a valid email address"



    }
}
