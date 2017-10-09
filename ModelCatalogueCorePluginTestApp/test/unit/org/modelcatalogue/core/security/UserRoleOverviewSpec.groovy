package org.modelcatalogue.core.security

import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataModel
import spock.lang.Specification

@TestFor(DataModel)
class UserRoleOverviewSpec extends Specification {

    def "dataModelName concatenates Data Model name and  defaultModelCatalogueId"() {
        when:
        List<UserRole> userRoleList = []
        UserRoleOverview userRoleOverview = UserRoleOverview.of(userRoleList)

        then:
        userRoleOverview.generalAuthorities.isEmpty()
        userRoleOverview.dataModelAuthorities.isEmpty()

        when:
        User user = new User(username: 'testuser')
        Role roleUser = new Role(authority: 'ROLE_USER')
        Role roleSupervisor = new Role(authority: 'ROLE_SUPERVISOR')
        DataModel dataModel = new DataModel(name: 'Cancer Outcomes and Services Dataset')
        dataModel.setId(2)
        userRoleList = [
                new UserRole(user: user, role: roleUser),
                new UserRole(user: user, role: roleSupervisor, dataModel: dataModel),
        ]
        userRoleOverview = UserRoleOverview.of(userRoleList)

        List<DataModelAuthorities> expected = [
                new DataModelAuthorities(id: 2, authorities: ['ROLE_SUPERVISOR'] as Set<String>)
        ]

        then:
        userRoleOverview.generalAuthorities == ['ROLE_USER'] as Set<String>
        userRoleOverview.dataModelAuthorities.size() == expected.size()
        userRoleOverview.dataModelAuthorities[0].id == expected[0].id
        userRoleOverview.dataModelAuthorities[0].authorities == expected[0].authorities
    }
}
