package org.modelcatalogue.core

import grails.converters.JSON
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.DataModelFilter

class UserController extends AbstractCatalogueElementController<User> {

    UserController() {
        super(User, false)
    }

    @Override
    protected String getRoleForSaveAndEdit() { "ADMIN" }

    def classifications() {
        if (!modelCatalogueSecurityService.isUserLoggedIn()) {
            notAuthorized()
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

    def favourite(Long id) {
        addRelation(id, 'favourite', true, null)
    }

}
