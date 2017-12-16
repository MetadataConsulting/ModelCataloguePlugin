package org.modelcatalogue.core.security

import groovy.transform.CompileStatic

@CompileStatic
class MetadataRolesUtils {

    public static final String ROLE_USER= 'ROLE_USER'
    public static final String ROLE_ADMIN = 'ROLE_ADMIN'
    public static final String ROLE_SUPERVISOR = 'ROLE_SUPERVISOR'
    public static final String ROLE_METADATA_CURATOR = 'ROLE_METADATA_CURATOR'

    static List<String> getRolesFromAuthority(String authority){
        if (authority == 'VIEWER') {
            return [ROLE_USER, ROLE_METADATA_CURATOR, ROLE_ADMIN, ROLE_SUPERVISOR]

        }  else if (authority == 'CURATOR') {

            return [ROLE_METADATA_CURATOR, ROLE_ADMIN, ROLE_SUPERVISOR]

        } else if (authority == "ADMIN") {
            return [ROLE_ADMIN, ROLE_SUPERVISOR]

        } else if (authority == "SUPERVISOR") {
            return [ROLE_SUPERVISOR]

        } else if (!authority.startsWith('ROLE_')) {
            return ["ROLE_${authority}" as String]
        }
        return [authority]
    }

    static String roles(String authority) {
        getRolesFromAuthority(authority).join(',')
    }

    static List<String> getRolesForDataModelAdministrationPermission() {
        [ROLE_USER, ROLE_METADATA_CURATOR]
    }

    static List<String> getRolesForDataModelReadPermission() {
        [ROLE_USER]
    }

}
