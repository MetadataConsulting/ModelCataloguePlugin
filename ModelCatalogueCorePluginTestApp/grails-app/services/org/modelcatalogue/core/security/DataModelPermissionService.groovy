package org.modelcatalogue.core.security

import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclUtilService
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.DefaultPermissionFactory
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.model.AccessControlEntry
import org.springframework.security.acls.model.MutableAcl
import org.springframework.security.acls.model.Permission

@CompileStatic
class DataModelPermissionService {

    DefaultPermissionFactory aclPermissionFactory

    AclUtilService aclUtilService

    AclService aclService


    void addPermission(DataModel dataModel, String username, int permission) {
        addPermission(username, dataModel.id, aclPermissionFactory.buildFromMask(permission))
    }

    void addPermission(DataModel dataModel, String username, Permission permission) {
        addPermission(username, dataModel.id, permission)
    }

    @PreAuthorize('hasRole("ROLE_SUPERVISOR")')
    @Transactional
    void addPermission(String username, Long dataModelId, Permission permission) {
        aclUtilService.addPermission(DataModel, dataModelId, username, permission)
    }

    @Transactional
    @PreAuthorize('hasRole("ROLE_SUPERVISOR") || hasPermission(#dataModel, admin)')
    void deletePermission(Long dataModelId, String username, Permission permission) {
        MutableAcl acl = (MutableAcl)aclUtilService.readAcl(DataModel, dataModelId)
        acl.entries.eachWithIndex { AccessControlEntry entry, int i ->
            if ( (entry.sid instanceof PrincipalSid) && ((PrincipalSid)entry.sid).principal == username && entry.permission == permission) {
                acl.deleteAce i
            }
        }
        aclService.updateAcl acl
    }

    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    List<UserPermissionList> findAllUserPermissions(Long dataModelId) {
        MutableAcl acl = (MutableAcl)aclUtilService.readAcl(DataModel, dataModelId)
        Map<String, UserPermissionList> m = [:] as HashMap<String, UserPermissionList>
        acl.entries.eachWithIndex { AccessControlEntry entry, int i ->
            if ( entry.sid instanceof PrincipalSid ) {
                String username = ((PrincipalSid)entry.sid).principal
                if ( !m.containsKey(username) ) {
                    m[username] = new UserPermissionList(username: username, permissionList: [])
                }
                m[username].permissionList.add(entry.permission)
            }

        }

        m.values() as List<UserPermissionList>
    }
}
