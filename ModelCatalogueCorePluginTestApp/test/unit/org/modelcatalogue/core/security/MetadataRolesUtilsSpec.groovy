package org.modelcatalogue.core.security

import spock.lang.Specification
import spock.lang.Unroll

import static org.modelcatalogue.core.security.MetadataRoles.*

class MetadataRolesUtilsSpec extends Specification {

    def "test getRolesForDataModelAdministrationPermission"() {
        expect:
        MetadataRolesUtils.getRolesForDataModelAdministrationPermission() == ['ROLE_USER', 'ROLE_METADATA_CURATOR']
    }

    def "test getRolesForDataModelReadPermission"() {
        expect:
        MetadataRolesUtils.getRolesForDataModelReadPermission() == ['ROLE_USER']
    }
}
