package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.security.UserGormService
import org.modelcatalogue.core.security.User

@CompileStatic
class UserCatalogueElementService extends AbstractCatalogueElementService {

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
