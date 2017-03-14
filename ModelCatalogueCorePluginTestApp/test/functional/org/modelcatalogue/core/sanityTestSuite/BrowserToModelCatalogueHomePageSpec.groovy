package org.modelcatalogue.core.sanityTestSuite

import geb.Browser
import geb.spock.GebSpec
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Created by Berthe on 13/03/2017.
 */
<<<<<<< Updated upstream
class browserToModelCatalogueHomePage extends GebSpec {

    void goToHomePage(){
        when:
=======
class BrowserToModelCatalogueHomePageSpec extends GebSpec {

    void goToHomePage(){
        when:
        // NAVIGATE TO MODEL CATALOGUE
>>>>>>> Stashed changes
        WebDriver driver = browser.driver
        go("https://gel-mc-test.metadata.org.uk/#/")

        then:
<<<<<<< Updated upstream
=======
        // VERIFY LOGIN IS PRESENT
>>>>>>> Stashed changes
        assert $("button",class:"btn btn-large btn-primary").text()== "Login"
        System.println("I found it")

    }
}
