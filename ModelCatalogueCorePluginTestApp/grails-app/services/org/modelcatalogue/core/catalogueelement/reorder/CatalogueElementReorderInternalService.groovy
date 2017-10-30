package org.modelcatalogue.core.catalogueelement.reorder

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.CatalogueElementGormService

class CatalogueElementReorderInternalService extends AbstractReorderInternalService {

    CatalogueElementGormService catalogueElementGormService

    @Override
    CatalogueElement findById(Long id) {
        catalogueElementGormService.findById(id)
    }
}
