package org.modelcatalogue.core

import grails.converters.JSON
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshallers

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

        User user = modelCatalogueSecurityService.currentUser

        user.filteredBy.each { Classification c ->
            user.removeFromFilteredBy(c)
        }

        if (!params.ids) {
            user.save(flush: true)
            redirect controller: 'user', action: 'current'
            return
        }
        Set<Long> ids = params.ids.toString().split(/\s*,\s*/).toList().collect{ it as Long }.toSet()
        ids.each { user.addToFilteredBy(Classification.get(it)) }
        user.save(flush: true)

        redirect controller: 'user', action: 'current'
    }

    def activity(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        User element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/activity", auditService.getChangesForUser(params, element))
    }

    def current() {
        if (!modelCatalogueSecurityService.currentUser) {
            render([success: false, username: null, roles: [], id: null, classifications: []] as JSON)
            return
        }

        render([
                success: true,
                username: modelCatalogueSecurityService.currentUser.username,
                roles: modelCatalogueSecurityService.currentUser.authorities*.authority,
                id: modelCatalogueSecurityService.currentUser.hasProperty('id') ? modelCatalogueSecurityService.currentUser.id : null,
                classifications: modelCatalogueSecurityService.currentUser.hasProperty('id') ? modelCatalogueSecurityService.currentUser.filteredBy?.collect{ CatalogueElementMarshallers.minimalCatalogueElementJSON(it) } : []
        ] as JSON)
    }

}
