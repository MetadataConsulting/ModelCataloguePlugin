package org.modelcatalogue.core.security

import groovy.transform.CompileStatic
import static org.modelcatalogue.core.security.MetadataRoles.*

@CompileStatic
class MetadataRolesUtils {

    static List<String> findAll() {
        [ROLE_USER, ROLE_CURATOR, ROLE_SUPERVISOR]
    }

    static List<String> getRolesForDataModelAdministrationPermission() {
        [ROLE_USER, ROLE_CURATOR]
    }

    static List<String> getRolesForDataModelReadPermission() {
        [ROLE_USER]
    }

}
