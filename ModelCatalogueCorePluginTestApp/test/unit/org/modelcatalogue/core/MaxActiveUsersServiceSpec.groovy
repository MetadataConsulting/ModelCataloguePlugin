package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.persistence.RoleGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.UserRoleGormService
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole
import spock.lang.Specification

@TestFor(MaxActiveUsersService)
@Mock([User, UserRole, Role])
class MaxActiveUsersServiceSpec extends Specification {

    def "numberOfUsersExceptUsersWithRole"() {
        given:
        Role role = new Role(authority: 'ROLE_SUPERVISOR')
        User supervisor = new User(username: 'supervisor')
        service.userRoleGormService = Stub(UserRoleGormService) {
            findAllByRole(_ as Role) >> [new UserRole(user: supervisor, role: role)]
        }
        service.userGormService = Stub(UserGormService) {
            countByEnabledAndUsernameNotInList(_, _) >> 1
        }

        expect:
        service.numberOfUsersExceptUsersWithRole(role) == 1
    }

    def "numberOfEnabledUsers"() {
        given:
        service.roleGormService = Stub(RoleGormService) {
            findByAuthority(_) >> null
        }
        service.userGormService = Stub(UserGormService) {
            countByEnabled(_) >> 2
        }

        expect:
        service.numberOfEnabledUsers() == 2
    }

    def "maxActiveUsers returns false if property mc.max.active.users is not set"() {
        expect:
        !service.maxActiveUsers()
    }

    def "maxActiveUsers"() {
        given:
        service.roleGormService = Stub(RoleGormService) {
            findByAuthority(_) >> null
        }
        service.userGormService = Stub(UserGormService) {
            countByEnabled(_) >> 2
        }

        when:
        service.maxUsers = 3

        then:
        !service.maxActiveUsers()

        when:
        service.maxUsers = 2

        then:
        service.maxActiveUsers()
    }
}
