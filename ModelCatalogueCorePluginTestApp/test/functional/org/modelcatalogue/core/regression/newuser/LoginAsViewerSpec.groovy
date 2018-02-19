package org.modelcatalogue.core.regression.newuser

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.WebDriver
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteB')  })
@Stepwise
class LoginAsViewerSpec extends AbstractModelCatalogueGebSpec {

    private static final String createButton = 'a#role_data-models_create-data-modelBtn'
    private static final String login = "button.btn"
    private static final String username = "input#username"
    private static final String password = "input#password"
    private static final String loginButton = "button.btn-success"

    def "login to model catalogue as a viewer"() {
        when:
        go(baseUrl)
        click login

        then:
        $("a.btn-block").text() == "Login with Google"

        when:'enter the username and password'
        fill(username) with("viewer")
        fill(password) with("viewer")

        and:'click on login button'
        click loginButton

        then:
        check createButton isMissing()
    }
}
