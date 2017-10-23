package org.modelcatalogue.core

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserGormService
import org.modelcatalogue.core.security.UserRole
import org.modelcatalogue.core.security.UserService
import org.modelcatalogue.core.util.DataModelFilter

class UserController extends AbstractCatalogueElementController<User> {

    UserService userService

    UserGormService userGormService

    SpringSecurityService springSecurityService

    FavouriteService favouriteService

    UserController() {
        super(User, false)
    }


    /**
     * Shows a single resource
     * @param id The id of the resource
     * @return The rendered resource or a 404 if it doesn't exist
     */
    def show(Long id) {

        List<String> roles = MetadataRolesUtils.getRolesFromAuthority('ADMIN')
        boolean requestedIdIsForTheLoggedUser = id == springSecurityService.principal.id

        if ( !(requestedIdIsForTheLoggedUser || SpringSecurityUtils.ifAnyGranted(roles.join(','))) ) {
            notFound()
            return
        }

        User element = findById(id)

        if (!element) {
            notFound()
            return
        }

        respond element
    }

    protected User findById(long id) {
        userGormService.findById(id)
    }

    @Override
    protected boolean allowSaveAndEdit() {
        modelCatalogueSecurityService.hasRole('SUPERVISOR')
    }

    def classifications() {
        if (!modelCatalogueSecurityService.isUserLoggedIn()) {
            unauthorized()
            return
        }

        DataModelFilter.from(request.JSON).to(modelCatalogueSecurityService.currentUser)

        redirect controller: 'user', action: 'current'
    }

    def current() {
        if ( !springSecurityService.isLoggedIn() ) {
            render([success: false, username: null, roles: [], id: null, classifications: []] as JSON)
            return
        }
        User currentUser = modelCatalogueSecurityService.currentUser
        DataModelFilter filter = DataModelFilter.from(currentUser)

        Map m = [
                success: true,
                username: currentUser.username,
                roles: currentUser.getAuthorities()*.authority,
                id: currentUser.hasProperty('id') ? currentUser.id : null,
                dataModels: filter.toMap()
        ]
        render(m as JSON)
    }

    def lastSeen() {
        if (!modelCatalogueSecurityService.hasRole('ADMIN')) {
            notFound()
            return
        }
        respond modelCatalogueSecurityService.usersLastSeen.sort { it.value }.collect { [username: it.key, lastSeen: new Date(it.value)] }.reverse()
    }

    def addFavourite() {
        Long dataModelId = request.JSON.id as Long
        favouriteService.favouriteModelById(dataModelId)
    }

    def removeFavourite() {
        Long dataModelId = request.JSON.id as Long
        favouriteService.unfavouriteModelById(dataModelId)
    }

    def enable() {
        switchEnabled(true)
    }

    def disable() {
        switchEnabled(false)
    }

    def role() {
        if (!modelCatalogueSecurityService.hasRole('ADMIN')) {
            notFound()
            return
        }

        User user = User.get(params.id)
        if (!user) {
            notFound()
            return
        }

        if (user.authorities.contains(UserService.ROLE_SUPERVISOR) || params.role == 'supervisor') {
            notFound()
            return
        }

        userService.redefineRoles(user, params.role)

        modelCatalogueSecurityService.logout(user.username)

        respond user
    }


    def apiKey(Boolean regenerate) {
        if (!modelCatalogueSecurityService.isUserLoggedIn()) {
            notFound()
            return
        }

        render([apiKey: userService.getApiKey(modelCatalogueSecurityService.currentUser, regenerate)] as JSON)
    }

    private switchEnabled(boolean enabled) {
        if (!modelCatalogueSecurityService.hasRole('SUPERVISOR')) {
            notFound()
            return
        }

        User user = User.get(params.id)
        if (!user) {
            notFound()
            return
        }

        if (user.authorities.contains(UserService.ROLE_SUPERVISOR)) {
            user.errors.rejectValue('enabled', 'user.cannot.edit.supervisor', 'Cannot edit supervisor account')
            respond user.errors
            return
        }

        user.enabled = enabled

        if (!user.save(flush: true)) {
            respond user.errors
            return
        }

        modelCatalogueSecurityService.logout(user.username)

        respond user
    }

    protected boolean hasAdditionalIndexCriteria() { return true }

    protected Closure buildAdditionalIndexCriteria() {
        return {
            not {
                'in' 'username', UserRole.findAllByRole(Role.findByAuthority(UserService.ROLE_SUPERVISOR))*.user.name
            }
        }
    }


}
