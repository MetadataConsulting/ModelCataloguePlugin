package org.modelcatalogue.core

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.integration.excel.ExcelLoader
import org.modelcatalogue.integration.excel.HeadersMap
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Issue
import spock.lang.Stepwise


@IgnoreIf({ !System.getProperty('geb.env') })
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
