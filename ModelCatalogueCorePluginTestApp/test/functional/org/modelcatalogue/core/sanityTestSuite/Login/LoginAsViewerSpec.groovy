package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.IgnoreIf
import spock.lang.Ignore

@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
class LoginAsViewerSpec extends AbstractModelCatalogueGebSpec {

    def "Create data Model button is not displayed for viewer"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('user', 'user')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage

        then:
        !dashboardPage.nav.createDataModelLink.isDisplayed()
    }
}
