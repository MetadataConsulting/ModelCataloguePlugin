package org.modelcatalogue.core.security

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.acl.AclUtilService
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.acls.model.Permission
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.transaction.annotation.Propagation

class DataModelAclService {

    AclUtilService aclUtilService

    SpringSecurityService springSecurityService

    UserDetailsService userDetailsService

    boolean hasReadPermission(Object instance) {
        DataModel dataModel = dataModelFromInstance(instance)
        if ( dataModel == null ) {
            return true
        }
        hasReadPermission(dataModel)
    }

    boolean hasReadPermission(DataModel dataModel, String username) {
        hasPermission(dataModel, username, [BasePermission.READ, BasePermission.ADMINISTRATION] as Permission[])
    }

    boolean hasAdministrationPermission(DataModel dataModel, String username) {
        hasPermission(dataModel, username, [BasePermission.ADMINISTRATION] as Permission[])
    }

    boolean hasReadPermission(DataModel dataModel) {
        hasPermission(dataModel, [BasePermission.READ, BasePermission.ADMINISTRATION] as Permission[])
    }

    boolean isAdminOrHasAdministrationPermission(Object instance) {
        if ( SpringSecurityUtils.ifAnyGranted(MetadataRoles.ROLE_SUPERVISOR) ) {
            return true
        }
        hasAdministratorPermission(instance)
    }

    boolean isAdminOrHasReadPermission(Object instance) {
        if ( SpringSecurityUtils.ifAnyGranted(MetadataRoles.ROLE_SUPERVISOR) ) {
            return true
        }
        hasReadPermission(instance)
    }

    boolean hasAdministratorPermission(Object instance) {
        DataModel dataModel = dataModelFromInstance(instance)
        if ( dataModel == null ) {
            return true
        }
        hasAdministratorPermission(dataModel)
    }

    boolean hasAdministratorPermission(DataModel dataModel) {
        hasPermission(dataModel, BasePermission.ADMINISTRATION)
    }

    boolean hasPermission(DataModel dataModel, Permission... permissions) {
        Authentication authentication = springSecurityService.authentication
        if ( authentication == null ) {
            return false
        }
        hasPermission(dataModel, authentication, permissions)
    }

    boolean hasPermission(DataModel dataModel, Authentication authentication, Permission... permissions) {
        if ( dataModel == null) {
            return true
        }

        aclUtilService.hasPermission(authentication, dataModel, permissions)
    }

    boolean hasPermission(DataModel dataModel, String username, Permission... permissions) {
        Authentication authentication = authenticationByUsername(username)
        hasPermission(dataModel, authentication, permissions)
    }

    Authentication authenticationByUsername(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username)
        new UsernamePasswordAuthenticationToken(userDetails, userDetails.password, userDetails.authorities)
    }

    boolean isAdminOrHasAdministratorPermission(DataModel dataModel) {
        if ( SpringSecurityUtils.ifAnyGranted(MetadataRoles.ROLE_SUPERVISOR) ) {
            return true
        }
        hasAdministratorPermission(dataModel)
    }

    protected DataModel dataModelFromInstance(Object instance) {
        if ( instance instanceof DataModel ) {
            return instance
        } else if ( instance instanceof CatalogueElement ) {
            return instance.dataModel
        }
        null
    }

    boolean isAdminOrHasAdministratorPermission(Object instance) {
        if ( SpringSecurityUtils.ifAnyGranted(MetadataRoles.ROLE_SUPERVISOR) ) {
            return true
        }

        DataModel dataModel = dataModelFromInstance(instance)

        if ( !dataModel ) {
            return true
        }
        hasAdministratorPermission(dataModel)
    }

    void addAdministrationPermission(DataModel dataModel) {
        addPermission(dataModel, BasePermission.ADMINISTRATION)
    }

    void addAdministrationPermission(Long dataModelId) {
        addPermission(dataModelId, BasePermission.ADMINISTRATION)
    }

    void addReadPermission(DataModel dataModel) {
        addPermission(dataModel, BasePermission.READ)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void addPermission(Long dataModelId, Permission permission) {
        String username = loggedUsername()
        if ( username == null ) {
            log.warn 'username is not set, cannot add permission'
            return
        }
        aclUtilService.addPermission(DataModel, dataModelId, username, permission)
    }

    @Transactional
    void addPermission(DataModel dataModel, Permission permission) {
        if ( hasPermission(dataModel, permission ) ) {
            return
        }
        String username = loggedUsername()
        if ( username == null ) {
            log.warn 'username is not set, cannot add permission'
            return
        }
        try {
            aclUtilService.addPermission(dataModel, username, permission)
        } catch ( NotFoundException e ) {
            log.warn 'NotFoundException captured while trying to add permission for data model: ' + (dataModel?.name ?: '') + " username: " + username
        }
    }

    void copyPermissions(DataModel sourceModel, DataModel destinationModel){
        if ( hasReadPermission(sourceModel) ) {
            addReadPermission(destinationModel)
        }
        if ( hasAdministratorPermission(sourceModel) ) {
            addAdministrationPermission(destinationModel)
        }
    }

    @CompileDynamic
    String loggedUsername() {
        if ( springSecurityService.principal instanceof String ) {
            return springSecurityService.principal
        }
        springSecurityService.principal?.username
    }

    void removePermissions(DataModel dataModel) {
        if ( hasReadPermission(dataModel) ) {
            aclUtilService.deletePermission(dataModel, loggedUsername(), BasePermission.READ)
        }
        if ( hasAdministratorPermission(dataModel) ) {
            aclUtilService.deletePermission(dataModel, loggedUsername(), BasePermission.ADMINISTRATION)
        }
    }
}
