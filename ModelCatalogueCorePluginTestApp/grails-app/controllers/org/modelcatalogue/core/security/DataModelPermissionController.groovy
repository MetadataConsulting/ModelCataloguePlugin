package org.modelcatalogue.core.security

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.springframework.security.acls.model.Permission

@CompileStatic
class DataModelPermissionController {

    static allowedMethods = [
            index: 'GET',
            show: 'GET',
            revoke: 'POST',
            grant: 'POST'
    ]

    DataModelGormService dataModelGormService

    DataModelPermissionService dataModelPermissionService

    UserGormService userGormService

    def index() {
        [rowList: dataModelGormService.findAllDataModelRows()]
    }

    def show() {
        Long id = params.long('id')
        List<String> usernameList = userGormService.findAllUsername()
        DataModel dataModel = dataModelGormService.findById(id)
        if ( !dataModel ) {
            redirect action: 'index'
            return
        }
        List<UserPermissionList> userPermissionsList = dataModelPermissionService.findAllUserPermissions(id)
        [
                usernameList: usernameList,
                dataModel: dataModel,
                userPermissionsList: userPermissionsList
        ]
    }

    def revoke(DataModelPermissionCommand cmd) {
        if ( cmd.hasErrors() ) {
            redirect(action: 'show', id: cmd.id)
            return
        }
        Permission permissionInstance = cmd.aclPermission()
        dataModelPermissionService.deletePermission(cmd.id, cmd.username, permissionInstance)
        redirect(action: 'show', id: cmd.id)
        return
    }

    def grant(DataModelPermissionCommand cmd) {
        if ( cmd.hasErrors() ) {
            redirect(action: 'show', id: cmd.id)
            return
        }
        Permission permissionInstance = cmd.aclPermission()
        MetadataResponseEvent responseEvent = dataModelPermissionService.addPermission(cmd.username, cmd.id, permissionInstance)
        String errorMessage = dataModelPermissionService.addPermissionErrorMessage(responseEvent, permissionInstance, request.locale)
        if ( errorMessage ) {
            flash.error = errorMessage
        }
        redirect(action: 'show', id: cmd.id)
        return
    }


}
