package org.modelcatalogue.core

import org.modelcatalogue.core.catalogueelement.addrelation.AbstractAddRelationService
import org.modelcatalogue.core.catalogueelement.addrelation.CatalogueElementAddRelationService
import org.modelcatalogue.core.catalogueelement.reorder.CatalogueElementReorderInternalService
import org.modelcatalogue.core.catalogueelement.searchwithinrelationships.AbstractSearchWithinRelationshipsService
import org.modelcatalogue.core.catalogueelement.searchwithinrelationships.CatalogueElementSearchWithinRelationshipsService
import org.modelcatalogue.core.catalogueelement.reorder.AbstractReorderInternalService
import org.modelcatalogue.core.persistence.CatalogueElementGormService

class CatalogueElementController extends AbstractCatalogueElementController<CatalogueElement> {

    CatalogueElementGormService catalogueElementGormService

    CatalogueElementSearchWithinRelationshipsService catalogueElementSearchWithinRelationshipsService

    CatalogueElementAddRelationService catalogueElementAddRelationService

    CatalogueElementReorderInternalService catalogueElementReorderInternalService

    CatalogueElementController() {
        super(CatalogueElement, true)
    }

    protected CatalogueElement findById(long id) {
        catalogueElementGormService.findById(id)
    }

    @Override
    protected AbstractReorderInternalService getReorderInternalService() {
        catalogueElementReorderInternalService
    }

    @Override
    protected AbstractAddRelationService getAddRelationService() {
        catalogueElementAddRelationService
    }

    @Override
    protected AbstractSearchWithinRelationshipsService getSearchWithinRelationshipsService() {
        catalogueElementSearchWithinRelationshipsService
    }
}
