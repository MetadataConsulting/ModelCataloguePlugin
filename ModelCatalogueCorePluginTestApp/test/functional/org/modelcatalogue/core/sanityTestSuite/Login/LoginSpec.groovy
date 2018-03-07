package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.IgnoreIf
import spock.lang.Unroll

@IgnoreIf({ !System.getProperty('geb.env') })
class LoginSpec extends AbstractModelCatalogueGebSpec {

    @Unroll
    void 'create button #description for #username'(String username, String password, boolean displayed, String description) {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login(username, password)

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage

        then:
        displayed == dashboardPage.nav.createDataModelLink.isDisplayed()

        where:
        username     | password     | displayed
        'user'       | 'user'       | false
        'supervisor' | 'supervisor' | true
        'curator'    | 'curator'    | true
        description = displayed ? 'is displayed' : 'is not displayed'
    }
}
