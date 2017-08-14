package org.modelcatalogue.core

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@Stepwise
class RegisterSpec extends AbstractModelCatalogueGebSpec {

    private static final String primaryButton = ".btn.btn-primary"

    def "try register"() {
        when:
            go "register/?email=test@me.com"

            $("#username-new").value("musketyr")
            $("#password").value("heslojaktram123!!!")
            $("#password2").value("heslojaktram123!!!")
            click primaryButton
        then:
            check ".alert.alert-info" displayed
    }

}
