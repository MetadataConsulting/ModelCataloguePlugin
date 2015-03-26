package org.modelcatalogue.core

import grails.converters.JSON
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.ClassificationFilter

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

        ClassificationFilter.from(request.JSON).to(modelCatalogueSecurityService.currentUser)

        redirect controller: 'user', action: 'current'
    }

    def current() {
        if (!modelCatalogueSecurityService.currentUser) {
            render([success: false, username: null, roles: [], id: null, classifications: []] as JSON)
            return
        }

        ClassificationFilter filter = ClassificationFilter.from(modelCatalogueSecurityService.currentUser)

        render([
                success: true,
                username: modelCatalogueSecurityService.currentUser.username,
                roles: modelCatalogueSecurityService.currentUser.authorities*.authority,
                id: modelCatalogueSecurityService.currentUser.hasProperty('id') ? modelCatalogueSecurityService.currentUser.id : null,
                classifications: filter.toMap()
        ] as JSON)
    }

}
