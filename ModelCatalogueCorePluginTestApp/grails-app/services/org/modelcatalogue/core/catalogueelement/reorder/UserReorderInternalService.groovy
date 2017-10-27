package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.UserGormService

@CompileStatic
class UserReorderInternalService extends AbstractReorderInternalService {
    UserGormService userGormService
    
    @Override
    CatalogueElement findById(Long id) {
        userGormService.findById(id)
    }
}
