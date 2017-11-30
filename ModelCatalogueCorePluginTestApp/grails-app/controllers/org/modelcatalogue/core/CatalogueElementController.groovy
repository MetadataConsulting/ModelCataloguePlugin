package org.modelcatalogue.core

import org.modelcatalogue.core.catalogueelement.CatalogueElementCatalogueElementService
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.persistence.CatalogueElementGormService

class CatalogueElementController extends AbstractCatalogueElementController<CatalogueElement> {

    CatalogueElementGormService catalogueElementGormService

    CatalogueElementCatalogueElementService catalogueElementCatalogueElementService

    CatalogueElementController() {
        super(CatalogueElement, true)
    }

    protected CatalogueElement findById(long id) {
        catalogueElementGormService.findById(id)
    }

    @Override
    protected ManageCatalogueElementService getManageCatalogueElementService() {
        catalogueElementCatalogueElementService
    }
}
