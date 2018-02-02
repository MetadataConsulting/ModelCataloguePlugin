package org.modelcatalogue.core.security

import spock.lang.Specification
import spock.lang.Unroll

import static org.modelcatalogue.core.security.MetadataRoles.*

class MetadataRolesUtilsSpec extends Specification {

    @Unroll
    def "#role #description"(String role, boolean expected, String description) {
        expect:
        expected == MetadataRolesUtils.isRoleDeprecated(role)

        where:
        role              | expected
        ROLE_USER         | false
        ROLE_CURATOR      | false
        ROLE_SUPERVISOR   | false
        ROLE_ADMIN        | true
        ROLE_STACKTRACE   | true
        ROLE_REGISTERED   | true
        description = expected ? 'is deprecated' : 'is not deprecated'
    }

    static boolean isRoleDeprecated(String role) {
        ( ROLE_ADMIN || ROLE_REGISTERED || ROLE_STACKTRACE )
    }

    def "For #authority expaned roles should be #expected"(String authority, List<String> expected) {

        expect:
        MetadataRolesUtils.getRolesFromAuthority(authority) == expected

        where:
        authority    | expected
        'VIEWER'     | ['ROLE_USER', 'ROLE_METADATA_CURATOR', 'ROLE_ADMIN', 'ROLE_SUPERVISOR']
        'CURATOR'    | ['ROLE_METADATA_CURATOR', 'ROLE_ADMIN', 'ROLE_SUPERVISOR']
        'ADMIN'      | ['ROLE_ADMIN', 'ROLE_SUPERVISOR']
        'SUPERVISOR' | ['ROLE_SUPERVISOR']
        'FOO'        | ['ROLE_FOO']
        'ROLE_ADMIN' | ['ROLE_ADMIN']

    }

    def "test getRolesForDataModelAdministrationPermission"() {
        expect:
        MetadataRolesUtils.getRolesForDataModelAdministrationPermission() == ['ROLE_USER', 'ROLE_METADATA_CURATOR']
    }

    def "test getRolesForDataModelReadPermission"() {
        expect:
        MetadataRolesUtils.getRolesForDataModelReadPermission() == ['ROLE_USER']
    }

    def "For #authority expaned roles as String should be #expected"(String authority, String expected) {

        expect:
        MetadataRolesUtils.roles(authority) == expected

        where:
        authority    | expected
        'VIEWER'     | 'ROLE_USER,ROLE_METADATA_CURATOR,ROLE_ADMIN,ROLE_SUPERVISOR'
        'CURATOR'    | 'ROLE_METADATA_CURATOR,ROLE_ADMIN,ROLE_SUPERVISOR'
        'ADMIN'      | 'ROLE_ADMIN,ROLE_SUPERVISOR'
        'SUPERVISOR' | 'ROLE_SUPERVISOR'
        'FOO'        | 'ROLE_FOO'
        'ROLE_ADMIN' | 'ROLE_ADMIN'

    }
}
