package org.modelcatalogue.core.security

import spock.lang.Specification

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
