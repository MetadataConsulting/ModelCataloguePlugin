
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


        // navigate to Model Catalogue

        when:
        driver = browser.driver
        driver.manage().deleteAllCookies()
        go(baseUrl)

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

        assert $("div.panel-body>div:nth-child(1)").text()== "Please provide a valid email address"



    }
}
