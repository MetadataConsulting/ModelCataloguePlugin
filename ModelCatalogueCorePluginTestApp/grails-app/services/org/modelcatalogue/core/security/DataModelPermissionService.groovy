package org.modelcatalogue.core.security

import grails.plugin.springsecurity.acl.AclService
import grails.plugin.springsecurity.acl.AclUtilService
import grails.transaction.Transactional
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.PermissionGrantedEvent
import org.modelcatalogue.core.events.UserMissingAnyGranted
import org.modelcatalogue.core.events.UserNotFoundEvent
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.domain.DefaultPermissionFactory
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.model.AccessControlEntry
import org.springframework.security.acls.model.MutableAcl
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.acls.model.Permission
import org.springframework.context.MessageSource

class DataModelPermissionService {

    public final static Set<String> AUTHORITIES_ALLOWED_TO_HAVE_ACL_ADMIN = [
            MetadataRolesUtils.ROLE_SUPERVISOR,
            MetadataRolesUtils.ROLE_ADMIN,
            MetadataRolesUtils.ROLE_METADATA_CURATOR,
    ] as Set<String>

    DefaultPermissionFactory aclPermissionFactory

    AclUtilService aclUtilService

    AclService aclService

    UserGormService userGormService

    UserRoleGormService userRoleGormService

    MessageSource messageSource

    void addPermission(DataModel dataModel, String username, int permission) {
        addPermission(username, dataModel.id, aclPermissionFactory.buildFromMask(permission))
    }

    void addPermission(DataModel dataModel, String username, Permission permission) {
        addPermission(username, dataModel.id, permission)
    }

    boolean aclAdminGrantAllowed(Set<Role> roles, Set<String> anyGranted) {
        List<String> authorities = roles*.authority
        authorities.any { anyGranted.contains(it) }
    }

    @PreAuthorize('hasRole("ROLE_SUPERVISOR")')
    @Transactional
    MetadataResponseEvent addPermission(String username, Long dataModelId, Permission permission) {
        User user = userGormService.findByUsername(username)
        if ( !user ) {
            return new UserNotFoundEvent(username: username)
        }
        Set<Role> roles = userRoleGormService.findRolesByUser(user)
        if ( BasePermission.ADMINISTRATION == permission && !aclAdminGrantAllowed(roles, AUTHORITIES_ALLOWED_TO_HAVE_ACL_ADMIN) ) {
            return new UserMissingAnyGranted(anyGranted: AUTHORITIES_ALLOWED_TO_HAVE_ACL_ADMIN)
        }

        aclUtilService.addPermission(DataModel, dataModelId, username, permission)

        new PermissionGrantedEvent()
    }

    String addPermissionErrorMessage(MetadataResponseEvent responseEvent, Permission permission, Locale locale) {
        if ( responseEvent instanceof UserNotFoundEvent ) {
            return messageSource.getMessage('user.error.notfound', [((UserNotFoundEvent)responseEvent).username] as Object[], 'User not Found', locale)

        } else if ( responseEvent instanceof UserMissingAnyGranted ) {
            return messageSource.getMessage('user.acl.grant.missingrole',
                    [permission.toString(), ((UserMissingAnyGranted) responseEvent).anyGranted] as Object[],
                    "Cannot grant ${permissionAsString(permission, locale)} permission. User does not have any of these roles ${((UserMissingAnyGranted) responseEvent).anyGranted}",
                    locale)
        }
        null
    }

    String permissionAsString(Permission permission, Locale locale) {
        if ( permission == BasePermission.ADMINISTRATION ) {
            return messageSource.getMessage('permission.administration', [] as Object[],"Administration", locale)
        } else if ( permission == BasePermission.READ ) {
            return messageSource.getMessage('permission.read', [] as Object[],"Read", locale)
        }
        permission.toString()
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
        try {
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
        } catch (NotFoundException notFoundException ) {
            return [] as List<UserPermissionList>
        }
    }
}
