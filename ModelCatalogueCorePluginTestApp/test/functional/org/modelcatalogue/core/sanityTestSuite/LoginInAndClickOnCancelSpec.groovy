package org.modelcatalogue.core.sanityTestSuite

<<<<<<< Updated upstream
/**
 * Created by Berthe on 13/03/2017.
 */
class LoginInSpec {
=======
import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 13/03/2017.
 */
class LoginInAndClickOnCancelSpec extends GebSpec {
    public static final String model = "div.panel-body>div"
    static WebDriver driver

    void clickOnCancel() {
        when:
       // navigate to model catalogue
        driver = browser.driver
        driver.manage().deleteAllCookies()
        go("https://gel-mc-test.metadata.org.uk/#/")
        $("button.btn").click()
        then:
        // verification that login page open
        assert $("div.modal-body>form>div:nth-child(1)>label").text()=="Username or Email"

        when:
        // enter your username and password
        $("input#username").value("viewer")
        find(By.cssSelector("input#password")).value("viewer")
        // click on cancel
        Thread.sleep(1000l)
        $("button.btn-warning").click()

        then:
        assert $("button",class:"btn btn-large btn-primary").text()== "Login"

    }
>>>>>>> Stashed changes
}
