package org.modelcatalogue.core

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.catalogueelement.UserCatalogueElementService
import org.modelcatalogue.core.persistence.RoleGormService
import org.modelcatalogue.core.persistence.UserRoleGormService
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.security.UserRole
import org.modelcatalogue.core.security.UserService
import org.modelcatalogue.core.util.DataModelFilter

class UserController extends AbstractCatalogueElementController<User> {

    UserService userService

    RoleGormService roleGormService

    UserGormService userGormService

    UserRoleGormService userRoleGormService

    SpringSecurityService springSecurityService

    FavouriteService favouriteService

    UserCatalogueElementService userCatalogueElementService

    UserController() {
        super(User, false)
    }

    /**
     * Shows a single resource
     * @param id The id of the resource
     * @return The rendered resource or a 404 if it doesn't exist
     */
    def show(Long id) {
        if ( !isAdminOrSupervisorOrLoggedUser(id) ) {
            unauthorized()
            return
        }

        User element = findById(id)

        if (!element) {
            notFound()
            return
        }

        respond element
    }

    protected isAdminOrSupervisorOrLoggedUser(Long id) {
        (id == springSecurityService.principal.id) || SpringSecurityUtils.ifAnyGranted(MetadataRoles.ROLE_SUPERVISOR)
    }

    protected User findById(long id) {
        userGormService.findById(id)
    }

    def classifications() {
        Long userId = springSecurityService.principal.id
        User user = userGormService.findById(userId)
        DataModelFilter.from(request.JSON).to(user)
        redirect controller: 'user', action: 'current'
    }

    def current() {
        if ( !springSecurityService.isLoggedIn() ) {
            render([success: false, username: null, roles: [], id: null, classifications: []] as JSON)
            return
        }

        Long userId = springSecurityService.principal.id
        User currentUser = userGormService.findById(userId)
        DataModelFilter filter = DataModelFilter.from(currentUser)

        Long dataModelId = params.long('dataModelId')
        List<String> roleList = currentRoleList(dataModelId)

        Map m = [
                success: true,
                username: currentUser.username,
                roles: roleList,
                id: currentUser.hasProperty('id') ? currentUser.id : null,
                dataModels: filter.toMap()
        ]
        render(m as JSON)
    }

    @CompileStatic
    List<String> currentRoleList(Long dataModelId) {
        List<String> roleList = []
        if ( SpringSecurityUtils.ifAllGranted(MetadataRoles.ROLE_SUPERVISOR) ) {
            roleList.addAll([MetadataRoles.ROLE_SUPERVISOR, 'ROLE_ADMIN', MetadataRoles.ROLE_CURATOR, MetadataRoles.ROLE_USER])

        } else if ( SpringSecurityUtils.ifAllGranted(MetadataRoles.ROLE_CURATOR) ) {
            roleList.addAll([MetadataRoles.ROLE_CURATOR, MetadataRoles.ROLE_USER])
        }
        roleList.addAll(uiRolesForDataModel(dataModelId))
        roleList
    }

    protected List<String> uiRolesForDataModel(Long dataModelId) {
        if ( dataModelId ) {
            DataModel dataModel = dataModelGormService.findById(dataModelId)
            if ( dataModelAclService.isAdminOrHasAdministrationPermission(dataModel) ) {
                return MetadataRolesUtils.getRolesForDataModelAdministrationPermission()

            } else if ( dataModelAclService.isAdminOrHasReadPermission(dataModel) ) {
                return MetadataRolesUtils.getRolesForDataModelReadPermission()
            }
        }
        []
    }

    def lastSeen() {
        respond modelCatalogueSecurityService.usersLastSeen.sort { it.value }.collect { [username: it.key, lastSeen: new Date(it.value)] }.reverse()
    }

    def addFavourite() {
        if ( !isAdminOrSupervisorOrLoggedUser(params.long('id')) ) {
            unauthorized()
            return
        }
        Long dataModelId = request.JSON.id as Long
        String elementType = request.JSON.elementType as String
        favouriteService.favouriteElementTypeById(elementType, dataModelId)
        render status: 200
    }

    def removeFavourite() {
        if ( !isAdminOrSupervisorOrLoggedUser(params.long('id')) ) {
            unauthorized()
            return
        }
        Long dataModelId = request.JSON.id as Long
        String elementType = request.JSON.elementType as String
        favouriteService.unfavouriteElementTypeById(elementType, dataModelId)
        render status: 200
    }

    def enable() {
        switchEnabled(true)
    }

    def disable() {
        switchEnabled(false)
    }

    def role() {
        User user = findById(params.long('id'))
        if (!user) {
            notFound()
            return
        }

        if (user.authorities.contains(MetadataRoles.ROLE_SUPERVISOR) || params.role == 'supervisor') {
            notFound()
            return
        }

        userService.redefineRoles(user, params.role)

        modelCatalogueSecurityService.logout(user.username)

        respond user
    }


    def apiKey(Boolean regenerate) {
        render([apiKey: userService.findApiKeyByUsername(springSecurityService.principal.username, regenerate ?: false)] as JSON)
    }


    private switchEnabled(boolean enabled) {

        long userId = params.long('id')
        User user = userGormService.switchEnabled(userId, enabled)
        if ( !user ) {
            notFound()
            return
        }
        if ( user.hasErrors() ) {
            respond user.errors
            return
        }

        modelCatalogueSecurityService.logout(user.username)

        respond user
    }

    @Override
    protected ManageCatalogueElementService getManageCatalogueElementService() {
        userCatalogueElementService
    }

    protected boolean hasAdditionalIndexCriteria() { return true }

    protected Closure buildAdditionalIndexCriteria() {
        return {
            not {
                'in' 'username', userRoleGormService.findAllByAuthority(MetadataRoles.ROLE_SUPERVISOR)*.user.name
            }
        }
    }
}
