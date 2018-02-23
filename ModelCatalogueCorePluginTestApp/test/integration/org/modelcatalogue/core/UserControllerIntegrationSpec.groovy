package org.modelcatalogue.core

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.User
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Unroll

class UserControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Override
    Map getPropertiesToEdit() {
        [name: "changedName", description: "edited description ", dataModel: dataModelForSpec]
    }

    @Override
    Map getNewInstance() {
        [name: 'user', username: 'user', password: 'password', dataModel: dataModelForSpec]
    }

    @Override
    Map getBadInstance() {
        [name: "t" * 300, description: "asdf", dataModel: dataModelForSpec]
    }

    @Override
    Class getResource() {
        User
    }

    @Override
    AbstractCatalogueElementController getController() {
        new UserController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    User getLoadItem() {
        User.findByName("Adam")
    }

    @Override
    User getAnotherLoadItem() {
        User.findByName("Bella")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkProperty(json.username, item.username, "username")
        checkProperty(json.email, item.email, "email")
        checkProperty(json.enabled, item.enabled, "enabled")
        checkProperty(json.accountExpired, item.accountExpired, "accountExpired")
        checkProperty(json.accountLocked, item.accountLocked, "accountLocked")
        checkProperty(json.passwordExpired, item.passwordExpired, "passwordExpired")

        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.username, inputItem.username, "username")
        checkProperty(json.email, inputItem.email, "email")

        return true
    }

    boolean isCheckVersion() {
        false
    }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 11, "${baseLink}?max=10&total=11&offset=10", ""],
                [2, 5, 5, 0, 11, "${baseLink}?max=5&total=11&offset=5", ""],
                [3, 5, 5, 5, 11, "${baseLink}?max=5&total=11&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 8, 11, "", "${baseLink}?max=4&total=11&offset=4"],
                [5, 2, 10, 10, 11, "", "${baseLink}?max=10&total=11&offset=0"],
                [6, 2, 2, 10, 11, "", "${baseLink}?max=2&total=11&offset=8"]
        ]
    }

    @Unroll
    def "currentRoleList for #roleList is #expected"(String username, List<String> roleList, List<String> expected) {
        setup:
        when:
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roleList as String[])
        SecurityContextHolder.context.setAuthentication(new TestingAuthenticationToken(username, "password", authorities))

        List<String> result = controller.currentRoleList(null)

        then:
        result.size() == expected.size()
        result == expected

        where:
        username     | roleList                        | expected
        'supervisor' | [MetadataRoles.ROLE_SUPERVISOR] | [MetadataRoles.ROLE_SUPERVISOR, 'ROLE_ADMIN', MetadataRoles.ROLE_CURATOR, MetadataRoles.ROLE_USER]
        'curator'    | [MetadataRoles.ROLE_CURATOR]    | [ MetadataRoles.ROLE_CURATOR, MetadataRoles.ROLE_USER]
    }
}
