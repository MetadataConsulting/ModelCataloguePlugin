package org.modelcatalogue.core.security

import grails.plugin.springsecurity.acl.AclUtilService
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.PermissionGrantedEvent
import org.modelcatalogue.core.events.AlreadyHasAclPermissionEvent
import org.modelcatalogue.core.events.UserMissingAnyGranted
import org.modelcatalogue.core.events.UserNotFoundEvent
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService
import org.springframework.context.MessageSource
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(DataModelPermissionService)
class DataModelPermissionServiceSpec extends Specification {

    @Unroll
    def "#authorities #description"(boolean expected, List<String> authorities, String description) {
        expect:
        Set<Role> roles = authorities.collect { new Role(authority: it) } as Set<Role>
        expected == service.aclAdminGrantAllowed(roles, DataModelPermissionService.AUTHORITIES_ALLOWED_TO_HAVE_ACL_ADMIN)

        where:
        expected | authorities
        true     | [MetadataRoles.ROLE_SUPERVISOR, MetadataRoles.ROLE_USER]
        true     | [MetadataRoles.ROLE_CURATOR, MetadataRoles.ROLE_USER]
        false    | [MetadataRoles.ROLE_USER]
        false    | []
        description = expected ? 'can be granted ACL ADMIN' : 'cannot be granted ACL ADMIN'
    }

    def "If user not found, addPermission returns UserNotFound event"() {
        given:
        service.userGormService = Mock(UserGormService)

        when:
        String username = 'steve'
        MetadataResponseEvent responseEvent = service.addPermission(username, 1L, BasePermission.ADMINISTRATION)

        then:
        responseEvent
        responseEvent instanceof UserNotFoundEvent
    }

    def "If user found but does not have required roles addPermission returns UserMissingAnyGranted"() {
        given:
        service.dataModelGormService = Mock(DataModelGormService)
        service.dataModelAclService = Stub(DataModelAclService) {
            hasAdministrationPermission(_,_) >> false
            hasReadPermission(_,_) >> false
        }
        service.userGormService = Stub(UserGormService) {
            findByUsername(_) >> new User()
        }
        service.userRoleGormService = Mock(UserRoleGormService)

        when:
        String username = 'steve'
        MetadataResponseEvent responseEvent = service.addPermission(username, 1L, BasePermission.ADMINISTRATION)

        then:
        responseEvent
        responseEvent instanceof UserMissingAnyGranted
    }

    def "If user found and it has required roles, but already has the ADMIN role return UnnecessaryOperationEvent"() {
        given:
        service.dataModelGormService = Mock(DataModelGormService)
        service.dataModelAclService = Stub(DataModelAclService) {
            hasAdministrationPermission(_,_) >> true
            hasReadPermission(_,_) >> false
        }
        service.userGormService = Stub(UserGormService) {
            findByUsername(_ as String) >> new User()
        }
        service.userRoleGormService = Stub(UserRoleGormService) {
            findRolesByUser(_ as User) >> [new Role(authority: MetadataRoles.ROLE_CURATOR)]
        }

        when:
        String username = 'steve'
        MetadataResponseEvent responseEvent = service.addPermission(username, 1L, BasePermission.ADMINISTRATION)

        then:
        responseEvent
        responseEvent instanceof AlreadyHasAclPermissionEvent
    }

    def "If user found and it has required roles, but already has the READ role return UnnecessaryOperationEvent"() {
        given:
        service.dataModelGormService = Mock(DataModelGormService)
        service.dataModelAclService = Stub(DataModelAclService) {
            hasAdministrationPermission(_,_) >> false
            hasReadPermission(_,_) >> true
        }
        service.userGormService = Stub(UserGormService) {
            findByUsername(_ as String) >> new User()
        }
        service.userRoleGormService = Stub(UserRoleGormService) {
            findRolesByUser(_ as User) >> [new Role(authority: MetadataRoles.ROLE_CURATOR)]
        }

        when:
        String username = 'steve'
        MetadataResponseEvent responseEvent = service.addPermission(username, 1L, BasePermission.READ)

        then:
        responseEvent
        responseEvent instanceof AlreadyHasAclPermissionEvent
    }

    def "If user found and it has required roles, addPermission grants permission and returns PermissionGrantedEvent"() {
        given:
        service.dataModelGormService = Mock(DataModelGormService)
        service.dataModelAclService = Stub(DataModelAclService) {
            hasAdministrationPermission(_,_) >> false
            hasReadPermission(_,_) >> false
        }
        service.userGormService = Stub(UserGormService) {
            findByUsername(_ as String) >> new User()
        }
        service.userRoleGormService = Stub(UserRoleGormService) {
            findRolesByUser(_ as User) >> [new Role(authority: MetadataRoles.ROLE_CURATOR)]
        }
        service.aclUtilService = Mock(AclUtilService) {
            1 * addPermission(DataModel, 1L, 'steve', BasePermission.ADMINISTRATION)
        }

        when:
        String username = 'steve'
        MetadataResponseEvent responseEvent = service.addPermission(username, 1L, BasePermission.ADMINISTRATION)

        then:
        responseEvent
        responseEvent instanceof PermissionGrantedEvent
    }

    def "If user found and we are trying to grant ROLE_USER, addPermission grants permission and returns PermissionGrantedEvent"() {
        given:
        service.dataModelGormService = Mock(DataModelGormService)
        service.dataModelAclService = Stub(DataModelAclService) {
            hasAdministrationPermission(_,_) >> false
            hasReadPermission(_,_) >> false
        }
        service.userGormService = Stub(UserGormService) {
            findByUsername(_ as String) >> new User()
        }
        service.userRoleGormService = Stub(UserRoleGormService) {
            findRolesByUser(_ as User) >> [new Role(authority: MetadataRoles.ROLE_USER)]
        }
        service.aclUtilService = Mock(AclUtilService) {
            1 * addPermission(DataModel, 1L, 'steve', BasePermission.READ)
        }

        when:
        String username = 'steve'
        MetadataResponseEvent responseEvent = service.addPermission(username, 1L, BasePermission.READ)

        then:
        responseEvent
        responseEvent instanceof PermissionGrantedEvent
    }

    @Unroll("#responseEvent #description")
    def "test addPermissionErrorMessage"(String expected, Permission permission, MetadataResponseEvent responseEvent, String description) {
        given:
        service.messageSource = Stub(MessageSource) {
            getMessage(_, _, _, _) >> 'message'
        }

        when:
        String result = service.addPermissionErrorMessage(responseEvent, permission, new Locale("en"))

        then:
        result == expected

        where:
        expected  | permission                    | responseEvent
        null      | BasePermission.ADMINISTRATION | new PermissionGrantedEvent()
        null      | BasePermission.ADMINISTRATION | new AlreadyHasAclPermissionEvent()
        'message' | BasePermission.ADMINISTRATION | new UserMissingAnyGranted()
        'message' | BasePermission.ADMINISTRATION | new UserNotFoundEvent()
        description = expected != null ? 'error message is returned' : 'no error message is return'
    }
}
