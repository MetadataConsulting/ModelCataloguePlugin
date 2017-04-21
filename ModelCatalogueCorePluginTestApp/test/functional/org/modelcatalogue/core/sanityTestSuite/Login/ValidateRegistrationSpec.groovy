package org.modelcatalogue.core.sanityTestSuite.Login


import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 13/03/2017.
 */
class ValidateRegistrationSpec extends  AbstractModelCatalogueGebSpec {
    private static final String signUP="a.btn"
    private static final String newUsername="input#username-new"
    private static final String newEmail="input#email-new"
    private static final String  password="input#password"
    private static final String  password2="input#password2"
    public static final String  createButton ="button.btn"
    private static final String  header = "div.panel-heading"
    private static final String  alert  ="div.alert"
    static WebDriver driver

    void goToRegistration(){

        when:
            // open browser
            driver = browser.driver
            // maximize window
            driver.manage().window().maximize()
            go baseUrl
             // click on sign up
             click signUP

        then:
            // verify the page title
            check( header).contains("Create a new account")


        when:
            //  enter new username
            fill newUsername with("tatiana")
           // type new email
           fill(newEmail)with("berthe.kuatche@metadataconsulting.co.uk")
           // type password
            fill(password)with("berthe32~~")
           // type the same password
           fill(password2)with("berthe32~~")
           // click pn create
            click createButton

        then:
           check(alert)displayed


    }

}
