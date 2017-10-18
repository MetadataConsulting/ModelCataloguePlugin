package org.modelcatalogue.core.security

import spock.lang.Specification

class MetadataRolesUtilsSpec extends Specification {

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
}
