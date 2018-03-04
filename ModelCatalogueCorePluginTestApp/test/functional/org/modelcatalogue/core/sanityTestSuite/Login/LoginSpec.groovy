package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.DataModelListPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Unroll

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteB')  })
class LoginSpec extends AbstractModelCatalogueGebSpec {

    @Unroll
    void 'create button #description for #username'(String username, String password, boolean displayed, String description) {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login(username, password)

        then:
        at DataModelListPage

        when:
        DataModelListPage dataModelListPage = browser.page DataModelListPage

        then:
        waitFor { !dataModelListPage.createNewButton.isDisplayed() }

        where:
        username     | password     | displayed
        'viewer'     | 'viewer'     | false
        'supervisor' | 'supervisor' | true
        'curator'    | 'curator'    | true
        description = displayed ? 'is displayed' : 'is not displayed'
    }
}
