package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.WebDriver


class LoginSpec extends AbstractModelCatalogueGebSpec {

    private static final String models ="li#my-models>a"

    void doLoginAndClickCheckBox (){
       when:
           loginViewer()

        then:
            check models is("My Models")




    }
}
