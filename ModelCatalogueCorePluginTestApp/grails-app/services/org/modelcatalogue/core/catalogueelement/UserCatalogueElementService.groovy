package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.security.User

@CompileStatic
class UserCatalogueElementService<T extends User> extends AbstractCatalogueElementService<User> {

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
