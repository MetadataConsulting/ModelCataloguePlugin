package org.modelcatalogue.core.catalogueelement.addrelation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.AssetGormService

@CompileStatic
class AssetAddRelationService extends AbstractAddRelationService {

    AssetGormService assetGormService

    @Override
    CatalogueElement findById(Long id) {
        assetGormService.findById(id)
    }
}
