package org.modelcatalogue.core

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.test.mixin.TestFor
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.User
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
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
}
