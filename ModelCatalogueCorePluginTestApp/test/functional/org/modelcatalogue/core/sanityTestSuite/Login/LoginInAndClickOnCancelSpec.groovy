package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginModalPage
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
class LoginInAndClickOnCancelSpec extends AbstractModelCatalogueGebSpec {

    void clickOnCancel() {
        when:
        HomePage homePage = to HomePage
        homePage.login()

        then:
        at LoginModalPage

        when:'enter the username and password'
        LoginModalPage loginModalPage = browser.page LoginModalPage
        loginModalPage.username = 'viewer'
        loginModalPage.password = 'viewer'

        and:'click on cancel'
        loginModalPage.cancel()

        then:
        at HomePage
    }
}