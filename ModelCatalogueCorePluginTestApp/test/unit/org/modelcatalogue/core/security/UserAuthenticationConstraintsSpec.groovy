package org.modelcatalogue.core.security

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(UserAuthentication)
class UserAuthenticationConstraintsSpec extends Specification {

    void 'username cannot be null'() {
        when:
        domain.username = null

        then:
        !domain.validate(['username'])
    }

    void 'authenticationDate cannot be null'() {
        when:
        domain.authenticationDate = null

        then:
        !domain.validate(['authenticationDate'])
    }
}
