package org.modelcatalogue.core.security

import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService
import org.modelcatalogue.core.util.LoggedUserUtils
import org.springframework.context.MessageSource
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.acls.model.Permission

@CompileStatic
class DataModelPermissionController {

    static allowedMethods = [
            index: 'GET',
            show: 'GET',
            revoke: 'POST',
            grant: 'POST'
    ]
    SpringSecurityService springSecurityService

    UserRoleGormService userRoleGormService

    DataModelGormService dataModelGormService

    DataModelPermissionService dataModelPermissionService

    UserGormService userGormService

    MessageSource messageSource

    def index() {
        [rowList: dataModelGormService.findAllDataModelRows()]
    }

    def show() {
        Long id = params.long('id')
        DataModel dataModel = dataModelGormService.findById(id)
        if ( !dataModel ) {
            redirect action: 'index'
            return
        }

        List<String> warningMessages = warningMessages()
        if ( warningMessages ) {
            flash.error = warningMessages.join(',')
        }
        showModel(id, dataModel)
    }

    protected Map showModel(Long id, DataModel dataModel = null) {
        List<String> usernameList = userGormService.findAllUsername()
        List<UserPermissionList> userPermissionsList = dataModelPermissionService.findAllUserPermissions(id)
        [
                usernameList: usernameList,
                dataModel: dataModel ?: dataModelGormService.findById(id),
                userPermissionsList: userPermissionsList
        ]
    }

    protected List<String> warningMessages() {
        Long loggedUserId = loggedUserId()
        List<String> warningMessages = []
        if (!userRoleGormService.hasRole(loggedUserId, MetadataRoles.ROLE_USER) ) {
            warningMessages << messageSource.getMessage('acluser.missing.roleUser', [MetadataRoles.ROLE_USER] as Object[], 'You miss role {0} necessary to grant/revoke ACL read permission', request.locale)
        }
        if (!userRoleGormService.hasRole(loggedUserId, MetadataRoles.ROLE_ADMIN) ) {
            warningMessages << messageSource.getMessage('acluser.missing.roleAdmin', [MetadataRoles.ROLE_ADMIN] as Object[], 'You miss role {0} necessary to grant/revoke ACL administration permission', request.locale)
        }
        if ( warningMessages ) {
            warningMessages << messageSource.getMessage('acluser.requirelogout', [] as Object[], 'Logout and Sign in again after granting the roles', request.locale)
        }
        warningMessages

    }

    protected Long loggedUserId() {
        Object principal = springSecurityService.principal
        LoggedUserUtils.id(principal) as Long
    }

    def revoke(DataModelPermissionCommand cmd) {
        if ( cmd.hasErrors() ) {
            redirect(action: 'show', id: cmd.id)
            return
        }
        try {
            Permission permissionInstance = cmd.aclPermission()
            dataModelPermissionService.deletePermission(cmd.id, cmd.username, permissionInstance)
        } catch(NotFoundException e) {
            flash.error = messageSource.getMessage('aclrevoke.notFoundException', [] as Object[], 'Not found exception raised while attempting to revoke a permission', request.locale)
            render view: 'show', model: showModel(cmd.id)
            return
        }

        render view: 'show', model: showModel(cmd.id)
    }

    def grant(DataModelPermissionCommand cmd) {
        if ( cmd.hasErrors() ) {
            redirect(action: 'show', id: cmd.id)
            return
        }
        Permission permissionInstance = cmd.aclPermission()
        try {
            MetadataResponseEvent responseEvent = dataModelPermissionService.addPermission(cmd.username, cmd.id, permissionInstance)
            String errorMessage = dataModelPermissionService.addPermissionErrorMessage(responseEvent, permissionInstance, request.locale)
            if ( errorMessage ) {
                flash.error = errorMessage
            }
        } catch(NotFoundException e) {
            flash.error = messageSource.getMessage('aclgrant.notFoundException', [] as Object[], 'Not found exception raised while attempting to grant a permission', request.locale)
            render view: 'show', model: showModel(cmd.id)
            return
        }

        render view: 'show', model: showModel(cmd.id)
    }
}
