package org.modelcatalogue.core.security

import groovy.transform.CompileStatic
import org.springframework.security.acls.model.Permission

@CompileStatic
class UserAndPermissionList {
    String username
    List<Permission> permissionList
}
