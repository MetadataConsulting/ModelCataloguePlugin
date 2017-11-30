package org.modelcatalogue.core.Regression

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
class InvalidRegistrationSpec extends GebSpec {

    public static final String model = "div.panel-body>div"
    public static final String signUp = "a.btn"
    static WebDriver driver

    void invalidRegistration() {


        when:'navigate to Model Catalogue and click on the signUP button'
        driver = browser.driver
        go(baseUrl)
        $(signUp).click()

        then:'verify the title'
        $("span", class: "mc-name").text() == "Model Catalogue"

        when:' enter username and email '
        $("input#username-new").value("tatiana")
        find(By.cssSelector("input#email-new")).value("berthe.kuatche@metadataconsulting")

        and:'type the password'
        $("input#password").value("berthe32~~")
        $("input#password2").value("berthe32~~")
        // CLICK ON menuButton
        $("button.btn").click()
        // WAIT FOR 1 MIN
        Thread.sleep(1000L)

        then:'VERIFY THIS TEXT PRESENT'
        $("div.panel-body>div:nth-child(1)").text() == "Please provide a valid email address"


    }
}
