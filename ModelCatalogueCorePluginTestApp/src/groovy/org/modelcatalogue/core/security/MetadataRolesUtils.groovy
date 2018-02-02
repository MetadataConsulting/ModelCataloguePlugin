package org.modelcatalogue.core.security

import groovy.transform.CompileStatic
import static org.modelcatalogue.core.security.MetadataRoles.*

@CompileStatic
class MetadataRolesUtils {

    static List<String> getRolesFromAuthority(String authority){
        if (authority == 'VIEWER') {
            return [ROLE_USER, ROLE_CURATOR, ROLE_ADMIN, ROLE_SUPERVISOR]

        }  else if (authority == 'CURATOR') {

            return [ROLE_CURATOR, ROLE_ADMIN, ROLE_SUPERVISOR]

        } else if (authority == "ADMIN") {
            return [ROLE_ADMIN, ROLE_SUPERVISOR]

        } else if (authority == "SUPERVISOR") {
            return [ROLE_SUPERVISOR]

        } else if (!authority.startsWith('ROLE_')) {
            return ["ROLE_${authority}" as String]
        }
        [authority]
    }

    static boolean isRoleDeprecated(String role) {
        [ROLE_ADMIN, ROLE_REGISTERED, ROLE_STACKTRACE].contains(role)
    }

    static String roles(String authority) {
        getRolesFromAuthority(authority).join(',')
    }

    static List<String> getRolesForDataModelAdministrationPermission() {
        [ROLE_USER, ROLE_CURATOR]
    }

    static List<String> getRolesForDataModelReadPermission() {
        [ROLE_USER]
    }

}
