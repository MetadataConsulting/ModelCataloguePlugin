package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 14/03/2017.
 */
class ResetPasswordSpec extends GebSpec {

    void doResetPassword(){
        when:
        // navigate to model catalogue
        WebDriver driver = browser.driver
        go(baseUrl)
        // click on login
        $("button.btn").click()
        then:
        // verify that username or email present on the page
        assert $("label",for:"username").text()=="Username or Email"

        when:
        // click on reset password button
        $("p.help-block>a").click()
        find(By.cssSelector("input#username-new")).value("berthe.kuatche@metadataconsulting.co.uk")
        find(By.cssSelector("button.btn")).click()


        then:
        // verify the error message
        //assert title=="Model Catalogue"
       //System.out.println( driver.getTitle())
        assert $("div.alert-danger").text()=="No user was found with that username"
        noExceptionThrown()

        when:
        // re- type the username
        find(By.cssSelector("input#username-new")).value("viewer")
        find(By.cssSelector("button.btn")).click()

        then:
        // verify the second error message
        assert $("div.alert-danger").text()=="Given user doesn't have any email associated. Please, contact the administrator."

    }



}
