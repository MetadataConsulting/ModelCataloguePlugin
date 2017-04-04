package org.modelcatalogue.core.sanityTestSuite.Login

import geb.spock.GebSpec
import groovy.mock.interceptor.Ignore
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.WebDriver

/**
 * Created by Berthe on 14/03/2017.
 */
class LoginAsViewerSpec extends AbstractModelCatalogueGebSpec {

    void doLoginAsViewer(){
        when:
        loginViewer()
        then:
        $("span.mc-name").text()=="Model Catalogue"
        noExceptionThrown()


    }
}
