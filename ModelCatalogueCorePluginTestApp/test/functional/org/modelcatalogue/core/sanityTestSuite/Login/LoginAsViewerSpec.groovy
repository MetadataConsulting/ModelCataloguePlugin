package org.modelcatalogue.core.sanityTestSuite.Login

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.DataModelListPage
import org.modelcatalogue.core.geb.LoginPage
import org.openqa.selenium.WebDriver
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteB')  })
@Stepwise
class LoginAsViewerSpec extends AbstractModelCatalogueGebSpec {

    def "Create data Model button is not displayed for viewer"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('viewer', 'viewer')

        then:
        at DataModelListPage

        when:
        DataModelListPage dataModelListPage = browser.page DataModelListPage

        then:
        waitFor { !dataModelListPage.createNewButton.isDisplayed() }
    }
}
