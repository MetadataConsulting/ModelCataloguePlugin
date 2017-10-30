package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.CatalogueElementGormService

class CatalogueElementSearchWithinRelationshipsService extends AbstractSearchWithinRelationshipsService {

    CatalogueElementGormService catalogueElementGormService

    @Override
    protected String resourceName() {
        'catalogueElement'
    }

    @Override
    CatalogueElement findById(Long id) {
        catalogueElementGormService.findById(id)
    }
}
