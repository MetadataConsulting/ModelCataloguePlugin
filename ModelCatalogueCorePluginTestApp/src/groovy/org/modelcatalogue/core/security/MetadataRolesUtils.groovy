package org.modelcatalogue.core.security

import groovy.transform.CompileStatic

@CompileStatic
class MetadataRolesUtils {

    static List<String> getRolesFromAuthority(String authority){
        if (authority == 'VIEWER') {
            return ['ROLE_USER', 'ROLE_METADATA_CURATOR', 'ROLE_ADMIN', 'ROLE_SUPERVISOR']

        }  else if (authority == 'CURATOR') {

            return ['ROLE_METADATA_CURATOR', 'ROLE_ADMIN', 'ROLE_SUPERVISOR']

        } else if (authority == "ADMIN") {
            return ['ROLE_ADMIN', 'ROLE_SUPERVISOR']

        } else if (authority == "SUPERVISOR") {
            return ['ROLE_SUPERVISOR']

        } else if (!authority.startsWith('ROLE_')) {
            return ["ROLE_${authority}" as String]
        }
        return [authority]
    }

}
