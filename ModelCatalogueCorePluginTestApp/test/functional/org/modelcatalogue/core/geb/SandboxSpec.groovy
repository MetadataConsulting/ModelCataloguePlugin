package org.modelcatalogue.core.geb

import geb.spock.GebSpec
import org.openqa.selenium.WebDriver

class SandboxSpec extends GebSpec {

    void doLogin() {
        when:
        WebDriver driver = browser.driver
        driver.get("http://www.gebish.org/manual/current/api/")


        then:
            noExceptionThrown()
    }

}
