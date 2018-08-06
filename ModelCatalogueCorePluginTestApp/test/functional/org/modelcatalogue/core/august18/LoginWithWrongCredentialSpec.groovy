package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1483')
@Title('Login with wrong credential')
@Stepwise
class UserCanCreateDataElementAndImportDataTypeSpec extends GebSpec {

    @Shared
    String dataTypeName = UUID.randomUUID().toString()

    def "login with wrong credentials and verify"() {
        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator123')
        then:
        loginPage.incorrecCredentialMessageDisabled()

    }

}