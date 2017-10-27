package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.security.User

@CompileStatic
class UserSearchWithinRelationshipsService extends AbstractSearchWithinRelationshipsService {

    UserGormService userGormService

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(User.class.name)
    }

    @Override
    CatalogueElement findById(Long id) {
        userGormService.findById(id)
    }
}
