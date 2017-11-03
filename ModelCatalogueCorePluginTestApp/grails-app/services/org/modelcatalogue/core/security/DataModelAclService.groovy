package org.modelcatalogue.core.security

import com.google.common.collect.ImmutableSet
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclUtilService
import groovy.transform.CompileDynamic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.DataModelFilter
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Acl
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy
import org.springframework.security.acls.model.Permission
import org.springframework.security.core.Authentication

class DataModelAclService {

    AclUtilService aclUtilService

    ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy

    AclService aclService

    SpringSecurityService springSecurityService


    boolean hasReadPermission(DataModel dataModel) {
        if ( dataModel == null) {
            return true
        }
        Authentication authentication = springSecurityService.authentication
        if ( authentication == null ) {
            return false
        }
        aclUtilService.hasPermission(authentication, dataModel, BasePermission.READ)
    }

    boolean hasAdministratorPermission(DataModel dataModel) {
        if ( dataModel == null) {
            return true
        }
        Authentication authentication = springSecurityService.authentication
        if ( authentication == null ) {
            return false
        }
        aclUtilService.hasPermission(authentication, dataModel, BasePermission.ADMINISTRATION)
    }

    boolean isAdminOrHasAdministratorPermission(DataModel dataModel) {
        if ( SpringSecurityUtils.ifAnyGranted(MetadataRolesUtils.roles('ADMIN')) ) {
            return true
        }
        hasAdministratorPermission(dataModel)
    }

    boolean isAdminOrHasAdministratorPermission(Object instance) {
        if ( SpringSecurityUtils.ifAnyGranted(MetadataRolesUtils.roles('ADMIN')) ) {
            return true
        }
        DataModel dataModel
        if ( instance instanceof DataModel ) {
            dataModel = instance as DataModel
        } else if ( dataModel instanceof CatalogueElement ) {
            dataModel = instance.dataModel
        }
        if ( !dataModel ) {
            return true
        }
        hasAdministratorPermission(dataModel)
    }

    void addAdministrationPermission(DataModel dataModel) {
        addPermission(dataModel, BasePermission.ADMINISTRATION)
    }

    void addReadPermission(DataModel dataModel) {
        addPermission(dataModel, BasePermission.READ)
    }

    void addPermission(DataModel dataModel, Permission permission) {
        ObjectIdentity objectIdentity = objectIdentityRetrievalStrategy.getObjectIdentity(dataModel)
        Acl acl = aclService.readAclById(objectIdentity)
        if ( !acl ) {
            aclService.createAcl(objectIdentity)
        }
        aclUtilService.addPermission(dataModel, loggedUsername(), permission)
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
        springSecurityService.principal.username
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
