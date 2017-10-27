package org.modelcatalogue.core.catalogueelement.addrelation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence. UserGormService

@CompileStatic
class UserAddRelationService extends AbstractAddRelationService {

    UserGormService  userGormService

    @Override
    CatalogueElement findById(Long id) {
        userGormService.findById(id)
    }
}
