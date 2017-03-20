package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import geb.spock.GebSpec
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 17/03/2017.
 */
class ClickOnCancelButton extends GebSpec {


    def"Login in to Model Catalogue"(){

        when:
        WebDriver driver = browser.driver
        go(baseUrl)
        $("button.btn").click()
        $("input#username") << "viewer"
        $("input#password") << "viewer"
        $("input",type:"checkbox").value(true)
        waitFor {120}
        $("input#password") << Keys.ENTER

        then:
        noExceptionThrown()




    }
}
