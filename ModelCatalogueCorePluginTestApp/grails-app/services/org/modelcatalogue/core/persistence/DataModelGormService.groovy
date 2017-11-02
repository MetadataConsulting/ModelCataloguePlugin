package org.modelcatalogue.core.persistence

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclUtilService
import groovy.transform.CompileDynamic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.springframework.security.acls.model.Acl
import org.springframework.security.acls.model.Permission
import org.modelcatalogue.core.util.DataModelFilter
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import com.google.common.collect.ImmutableSet

class DataModelGormService {

    AclUtilService aclUtilService

    ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy

    AclService aclService

    SpringSecurityService springSecurityService

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAll() {
        DataModel.findAll()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllInIdList(List<Long> dataModelIdList) {
        DataModel.where { id in dataModelIdList }.list()
    }

    @PreAuthorize("hasPermission(#id, 'org.modelcatalogue.core.DataModel', read) or hasPermission(#id, 'org.modelcatalogue.core.DataModel', admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    @Transactional
    DataModel findById(long id) {
        DataModel.get(id)
    }

    boolean hasAccessToEveryDataModelInFilterIncludes(DataModelFilter dataModelFilter) {
        ImmutableSet<Long> dataModelIds = dataModelFilter.includes
        List<Long> dataModelIdList = dataModelIds.toList()
        List<DataModel> dataModelList = findAllInIdList(dataModelIdList)
        ( dataModelList.size() == dataModelIdList.size() )
    }

    boolean hasReadPermission(DataModel dataModel) {
        if ( dataModel == null) {
            return true
        }
        aclUtilService.hasPermission(springSecurityService.authentication, dataModel, BasePermission.READ)
    }

    boolean hasAdministratorPermission(DataModel dataModel) {
        if ( dataModel == null) {
            return true
        }
        aclUtilService.hasPermission(springSecurityService.authentication, dataModel, BasePermission.ADMINISTRATION)
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
}
