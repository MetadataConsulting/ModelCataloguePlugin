package org.modelcatalogue.core

import grails.converters.JSON
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole
import org.modelcatalogue.core.security.UserService
import org.modelcatalogue.core.util.DataModelFilter

class UserController extends AbstractCatalogueElementController<User> {

    UserService userService

    UserController() {
        super(User, false)
    }

    @Override
    protected boolean allowSaveAndEdit() {
        modelCatalogueSecurityService.hasRole('ADMIN')
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
        if (!modelCatalogueSecurityService.currentUser) {
            render([success: false, username: null, roles: [], id: null, classifications: []] as JSON)
            return
        }

        DataModelFilter filter = DataModelFilter.from(modelCatalogueSecurityService.currentUser)

        render([
                success: true,
                username: modelCatalogueSecurityService.currentUser.username,
                roles: modelCatalogueSecurityService.currentUser.authorities*.authority,
                id: modelCatalogueSecurityService.currentUser.hasProperty('id') ? modelCatalogueSecurityService.currentUser.id : null,
                dataModels: filter.toMap()
        ] as JSON)
    }

    def lastSeen() {
        if (!modelCatalogueSecurityService.hasRole('ADMIN')) {
            notFound()
            return
        }
        respond modelCatalogueSecurityService.usersLastSeen.sort { it.value }.collect { [username: it.key, lastSeen: new Date(it.value)] }.reverse()
    }

    def addFavourite(Long id) {
        addRelation(id, 'favourite', true, null)
    }

    def removeFavourite(Long id) {
        removeRelation(id, 'favourite', true, null)
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

    private switchEnabled(boolean enabled) {
        if (!modelCatalogueSecurityService.hasRole('ADMIN')) {
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
