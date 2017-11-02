package org.modelcatalogue.core.security

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(DataModelAclService)
class DataModelAclServiceSpec extends Specification {

    def "hasReadPermission returns true for null object"() {
        expect:
        service.hasReadPermission(null)
    }

    def "hasAdministratorPermission returns true for null object"() {
        expect:
        service.hasAdministratorPermission(null)
    }
}
