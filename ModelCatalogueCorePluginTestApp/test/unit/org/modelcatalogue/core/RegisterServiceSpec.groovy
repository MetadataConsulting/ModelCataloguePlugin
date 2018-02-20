package org.modelcatalogue.core

import grails.plugin.springsecurity.ui.RegistrationCode
import grails.test.mixin.TestFor
import org.modelcatalogue.core.security.User
import spock.lang.Specification

@TestFor(RegisterService)
class RegisterServiceSpec extends Specification {

    def "if maxActiveUsers registers return null"() {
        given:
        service.maxActiveUsersService = Stub(MaxActiveUsersService) {
            maxActiveUsers() >> true
        }

        expect:
        service.register(new User(username: 'supervisor')) == null

        and:
        service.register('supervisor', 'supervisor', 'supervisor@email.com') == null
    }
}
