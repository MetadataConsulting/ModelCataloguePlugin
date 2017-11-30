package org.modelcatalogue.core

import org.modelcatalogue.core.security.DataModelPermissionCommand
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import spock.lang.Specification
import spock.lang.Unroll

class DataModelPermissionCommandSpec extends Specification {

    @Unroll
    def "extract Permission class from #permission string"(String permission, Permission expected) {
        when:
        DataModelPermissionCommand cmd = new DataModelPermissionCommand(permission: permission)
        Permission result = cmd.aclPermission()

        then:
        result.mask == expected.mask

        where:
        permission       | expected
        'administration' | BasePermission.ADMINISTRATION
        'read'           | BasePermission.READ
        'create'         | BasePermission.CREATE
        'delete'         | BasePermission.DELETE
        'write'          | BasePermission.WRITE
    }
}
