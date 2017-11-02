package org.modelcatalogue.core.persistence

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(DataModelGormService)
class DataModelGormServiceSpec extends Specification {

    def "hasReadPermission returns true for null object"() {
        expect:
        service.hasReadPermission(null)
    }

    def "hasAdministratorPermission returns true for null object"() {
        expect:
        service.hasAdministratorPermission(null)
    }
}
