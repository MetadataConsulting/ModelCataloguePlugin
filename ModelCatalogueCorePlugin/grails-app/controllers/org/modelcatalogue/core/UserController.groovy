package org.modelcatalogue.core

import grails.converters.JSON
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshallers

class UserController extends AbstractCatalogueElementController<User> {

    def dataArchitectService

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

        User user = modelCatalogueSecurityService.currentUser
        user.classifications?.clear()

        if (!params.ids) {
            user.save(flush: true)
            redirect controller: 'user', action: 'current'
            return
        }
        Set<Long> ids = params.ids.toString().split(/\s*,\s*/).toList().collect{ it as Long }.toSet()
        ids.each { user.addToClassifications(Classification.get(it)) }
        user.save(flush: true)

        redirect controller: 'user', action: 'current'
    }

    def current() {
        if (!modelCatalogueSecurityService.isUserLoggedIn()) {
            notAuthorized()
            return
        }
        render([
                success: true,
                username: modelCatalogueSecurityService.currentUser.username,
                roles: modelCatalogueSecurityService.currentUser.authorities*.authority,
                id: modelCatalogueSecurityService.currentUser.hasProperty('id') ? modelCatalogueSecurityService.currentUser.id : null,
                classifications: modelCatalogueSecurityService.currentUser.hasProperty('id') ? modelCatalogueSecurityService.currentUser.classifications?.collect{ CatalogueElementMarshallers.minimalCatalogueElementJSON(it) } : []
        ] as JSON)
    }

}
