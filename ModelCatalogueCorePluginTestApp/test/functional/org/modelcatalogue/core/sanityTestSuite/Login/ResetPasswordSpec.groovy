package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 14/03/2017.
 */
class ResetPasswordSpec extends AbstractModelCatalogueGebSpec {
    private static final String login ="button.btn"
    private static final String resetPassword ="p.help-block>a"
    private static final String email ="input#username-new"
    private static final String resetMyPassword ="button.btn"

    void doResetPassword(){
        when:
             // navigate to model catalogue
             WebDriver driver = browser.driver
             go(baseUrl)
             // click on login
             click login
        then:
             // verify that username or email present on the page
              $("label",for:"username").text()=="Username or Email"

        when:
             // click on reset password button
             click resetPassword
        then:
             // verify header
             $("div.panel-heading").text()=="Forgot Password"

        when:
               // enter email address
               fill(email)with("berthe.kuatche@metadataconsulting.co.uk")
               // click on reset
               click resetMyPassword


        then:
             // verify the error message
              $("div.alert-danger").text()=="No user was found with that username"


        when:
             // re- type the username
             fill(email)with("viewer")
            // click rest my password button
            click resetMyPassword

        then:
             // verify the second error message
            $("div.alert-danger").text()=="Given user doesn't have any email associated. Please, contact the administrator."



    }



}
