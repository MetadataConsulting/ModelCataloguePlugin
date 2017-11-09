package org.modelcatalogue.core.security

import groovy.transform.CompileStatic
import org.springframework.security.acls.model.Permission

@CompileStatic
class UserPermissionList {
    String username
    List<Permission> permissionList
}
