package org.modelcatalogue.core.security

import org.springframework.security.acls.model.Permission

class DataModelPermissionTagLib {

    static namespace = "sec"

    def permissionAsString = { attrs, body ->
        if ( attrs.permission && attrs.permission instanceof Permission ) {
            Permission permission = attrs.permission as Permission
            switch (permission.mask ) {
                case 1:
                    out << 'Read'

                    break
                case 2:
                    out << 'Write'

                    break
                case 4:
                    out << 'Create'
                    break

                case 8:
                    out << 'Delete'

                    break
                case 16:
                    out << 'Administration'
                    break
            }
        }
    }
}
