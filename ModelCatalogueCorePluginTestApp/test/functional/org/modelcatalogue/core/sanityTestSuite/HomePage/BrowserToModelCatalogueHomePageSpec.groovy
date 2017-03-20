package org.modelcatalogue.core.sanityTestSuite.HomePage

import geb.Browser
import geb.spock.GebSpec
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Created by Berthe on 13/03/2017.
 */


class BrowserToModelCatalogueHomePageSpec extends GebSpec {

    void goToHomePage(){
        when:
        // NAVIGATE TO MODEL CATALOGUE

        WebDriver driver = browser.driver
        go(baseUrl)

        then:
        // VERIFY LOGIN IS PRESENT

        assert $("button",class:"btn btn-large btn-primary").text()== "Login"
        System.println("I found it")

    }
}
