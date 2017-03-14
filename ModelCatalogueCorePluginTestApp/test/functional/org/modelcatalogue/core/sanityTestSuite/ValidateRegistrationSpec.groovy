package org.modelcatalogue.core.sanityTestSuite


import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 13/03/2017.
 */
class ValidateRegistrationSpec extends GebSpec {
    public static final String model ="div.panel-body>div"
    static WebDriver driver

    void goToRegistration(){

        when:
        driver = browser.driver
        driver.manage().deleteAllCookies()
        go("https://gel-mc-test.metadata.org.uk/#/")
        $("a.btn").click()
        then:
        assert $("span",class:"mc-name").text()=="Model Catalogue"

        when:
        $("input#username-new").value("tatiana")
        find(By.cssSelector("input#email-new")).value("berthe.kuatche@metadataconsulting.co.uk")
        $("input#password").value("berthe32~~")
        $("input#password2").value("berthe32~~")
        $("button.btn").click()

        then:
        noExceptionThrown()

    }

}
