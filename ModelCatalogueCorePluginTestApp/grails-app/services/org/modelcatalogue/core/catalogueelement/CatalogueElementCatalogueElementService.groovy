package org.modelcatalogue.core.catalogueelement

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.CatalogueElementGormService

class CatalogueElementCatalogueElementService extends AbstractCatalogueElementService {

    @Override
    protected String resourceName() {
        'catalogueElement'
    }

    @Override
    CatalogueElement findById(Long id) {
        catalogueElementGormService.findById(id)
    }
}
