package org.modelcatalogue.core.security
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import grails.test.mixin.TestFor

@TestFor(ApiRegisterController)
class ApiRegisterCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    ApiRegisterCommand cmd = new ApiRegisterCommand()

    void 'username cannot be null'() {
        when:
        cmd.username = null

        then:
        !cmd.validate(['username'])
        cmd.errors['username'].code == 'nullable'
    }

    void 'password cannot be null'() {
        when:
        cmd.password = null

        then:
        !cmd.validate(['password'])
        cmd.errors['password'].code == 'nullable'
    }

    void 'email cannot be null'() {
        when:
        cmd.email = null

        then:
        !cmd.validate(['email'])
        cmd.errors['email'].code == 'nullable'
    }

    void 'username cannot be blank'() {
        when:
        cmd.username = ''

        then:
        !cmd.validate(['username'])
        cmd.errors['username'].code == 'blank'
    }

    void 'password cannot be blank'() {
        when:
        cmd.password = ''

        then:
        !cmd.validate(['password'])
        cmd.errors['password'].code == 'blank'
    }

    void 'email cannot be blank'() {
        when:
        cmd.email = ''

        then:
        !cmd.validate(['email'])
        cmd.errors['email'].code == 'blank'
    }

    void 'password regex and length'() {
        when:
        cmd.password = 'foofao123'

        then:
        !cmd.validate(['password'])

        when:
        cmd.password = 'foofa#123'

        then:
        cmd.validate(['password'])
    }

    void 'password length'() {
        when:
        cmd.password = 'f#3'

        then:
        !cmd.validate(['password'])
    }
}
