package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.AssetGormService

@CompileStatic
class AssetReorderInternalService extends AbstractReorderInternalService {
    AssetGormService assetGormService

    @Override
    CatalogueElement findById(Long id) {
        assetGormService.findById(id)
    }
}
