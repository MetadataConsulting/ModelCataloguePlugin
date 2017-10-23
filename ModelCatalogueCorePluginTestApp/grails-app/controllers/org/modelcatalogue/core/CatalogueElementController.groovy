package org.modelcatalogue.core

import org.modelcatalogue.core.persistence.CatalogueElementGormService

class CatalogueElementController extends AbstractCatalogueElementController<CatalogueElement> {

    CatalogueElementGormService catalogueElementGormService

    CatalogueElementController() {
        super(CatalogueElement, true)
    }

    protected CatalogueElement findById(long id) {
        return null
    }
}
