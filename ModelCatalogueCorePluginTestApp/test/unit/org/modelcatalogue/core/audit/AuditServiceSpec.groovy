package org.modelcatalogue.core.audit

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.test.mixin.TestFor
import org.springframework.security.core.GrantedAuthority
import spock.lang.Specification

@TestFor(AuditService)
class AuditServiceSpec extends Specification {

    def "if springSecurityService.principal is GrailsUser return id"() {
        given:
        service.springSecurityService = Stub(SpringSecurityService) {
            getPrincipal() >> new GrailsUser('supervisor',
                    'supervisor',
                    true,
                    false,
                    false,
                    false,
                    [] as Collection<GrantedAuthority>,
                    10)
        }

        expect:
        service.loggedUserId() == 10
    }
}
