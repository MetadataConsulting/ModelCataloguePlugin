package org.modelcatalogue.core.persistence

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.acl.AclUtilService
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.util.DataModelFilter
import org.springframework.security.acls.domain.BasePermission
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import com.google.common.collect.ImmutableSet

class DataModelGormService {

    AclUtilService aclUtilService

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
        aclUtilService.hasPermission(springSecurityService.authentication, dataModel, BasePermission.READ)
    }

    boolean hasAdministratorPermission(DataModel dataModel) {
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

}
