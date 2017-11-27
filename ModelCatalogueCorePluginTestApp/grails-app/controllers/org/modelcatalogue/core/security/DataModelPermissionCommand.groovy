package org.modelcatalogue.core.security

import grails.validation.Validateable
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission

@Validateable
class DataModelPermissionCommand {
    Long id
    String username
    String permission

    static constraints = {
        id nullable: false, blank: false
        username nullable: false, blank: false
        permission nullable: false, inList: ['administration', 'delete', 'write', 'create', 'read']
    }

    Permission aclPermission() {
        switch (permission?.toLowerCase()) {

            case 'administration':
                return BasePermission.ADMINISTRATION

            case 'delete':
                return BasePermission.DELETE

            case 'write':
                return BasePermission.WRITE

            case 'create':
                return BasePermission.CREATE

            case 'read':
                return BasePermission.READ
        }

        null
    }
}
