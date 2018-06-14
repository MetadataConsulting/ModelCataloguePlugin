package org.modelcatalogue.core.datamodel.utilities

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.security.UserRep
import spock.lang.Stepwise
import spock.lang.Unroll

/**
 * Login. override getLoginUser() implementing class to say which user to login as.
 */
@Stepwise
abstract class LoginSpec extends GebSpec {

    abstract UserRep getLoginUser() // {return UserRep.USER}

    @Unroll
    def "Login as #username"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login(username, password)
        then:
        at DashboardPage

        where:
        username << [getLoginUser().username]
        password << [getLoginUser().password]
    }
}
