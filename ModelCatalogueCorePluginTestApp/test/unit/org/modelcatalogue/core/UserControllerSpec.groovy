package org.modelcatalogue.core

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.test.mixin.TestFor
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.MetadataRolesUtils
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(UserController)
class UserControllerSpec extends Specification {

    def "test uiRolesForDataModel"() {
        given:
        controller.dataModelGormService = Mock(DataModelGormService)

        when:
        controller.dataModelAclService = Stub(DataModelAclService) {
            isAdminOrHasAdministrationPermission(_) >> true
        }
        List<String> roles = controller.uiRolesForDataModel(1L)

        then:
        roles == ['ROLE_USER', 'ROLE_METADATA_CURATOR']

        when:
        controller.dataModelAclService = Stub(DataModelAclService) {
            isAdminOrHasAdministrationPermission(_) >> false
            isAdminOrHasReadPermission(_) >> true
        }
        roles = controller.uiRolesForDataModel(1L)

        then:
        roles == ['ROLE_USER']

        when:
        controller.dataModelAclService = Stub(DataModelAclService) {
            isAdminOrHasAdministrationPermission(_) >> false
            isAdminOrHasReadPermission(_) >> false
        }
        roles = controller.uiRolesForDataModel(1L)

        then:
        roles == []
    }

    @IgnoreRest
    @Unroll
    def "currentRoleList for #roleList is #expected"(List<String> roleList, List<String> expected) {
        given:
        SpringSecurityUtils.metaClass.static.ifAllGranted = { String role ->
            if ( roleList.contains(role) ) {
                return true
            }
            false
        }

        when:
        List<String> result = controller.currentRoleList(null)

        then:
        result.size() == expected.size()
        result == expected

        where:
        roleList                        | expected
        [MetadataRoles.ROLE_SUPERVISOR] | [MetadataRolesUtils.ROLE_SUPERVISOR, MetadataRolesUtils.ROLE_ADMIN, MetadataRolesUtils.ROLE_METADATA_CURATOR]
        [MetadataRoles.ROLE_ADMIN]      | [MetadataRolesUtils.ROLE_ADMIN, MetadataRolesUtils.ROLE_METADATA_CURATOR]
        [MetadataRoles.ROLE_CURATOR]    | [MetadataRolesUtils.ROLE_METADATA_CURATOR]
    }
}
