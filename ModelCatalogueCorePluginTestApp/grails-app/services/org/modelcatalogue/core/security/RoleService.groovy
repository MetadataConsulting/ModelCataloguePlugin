package org.modelcatalogue.core.security

import grails.transaction.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class RoleService {

    public static final String ROLE_USER = 'ROLE_USER'
    public static final String ROLE_SUPERVISOR = 'ROLE_SUPERVISOR'
    public static final String ROLE_METADATA_CURATOR = 'ROLE_METADATA_CURATOR'

    public static final List<String> SPECIFIC_ROLES = [ROLE_USER, ROLE_SUPERVISOR, ROLE_METADATA_CURATOR]

    @Transactional(readOnly = true)
    List<Role> findAllSpecificRoles() {
        Role.where {
            authority in SPECIFIC_ROLES
        }.list()
    }
}
