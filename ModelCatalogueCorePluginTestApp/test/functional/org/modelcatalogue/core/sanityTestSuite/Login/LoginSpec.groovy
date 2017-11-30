package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.WebDriver
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
class LoginSpec extends AbstractModelCatalogueGebSpec {

    private static final String createButton = 'a#role_data-models_create-data-modelBtn'
    private static final String adminTag = 'span.fa-cog'

    void doLoginAndClickCheckBox() {
       when:
           loginViewer()

        then:
            check createButton isMissing()
    }

    def "login to model catalogue as a curator"() {

        when:
        loginCurator()

        then:
        check adminTag isMissing()
    }

    def "login to model catalogue as an admin"() {

        when:
        loginAdmin()

        then:
        check adminTag isDisplayed()
    }
}
