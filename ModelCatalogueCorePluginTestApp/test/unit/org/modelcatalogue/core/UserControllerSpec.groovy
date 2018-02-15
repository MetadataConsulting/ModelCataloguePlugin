package org.modelcatalogue.core

import grails.test.mixin.TestFor
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.DataModelAclService
import spock.lang.Specification

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
