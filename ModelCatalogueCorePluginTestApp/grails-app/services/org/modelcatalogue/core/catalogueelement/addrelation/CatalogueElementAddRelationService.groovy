package org.modelcatalogue.core.catalogueelement.addrelation

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.CatalogueElementGormService

class CatalogueElementAddRelationService extends AbstractAddRelationService {

    CatalogueElementGormService catalogueElementGormService

    @Override
    CatalogueElement findById(Long id) {
        catalogueElementGormService.findById(id)
    }
}
