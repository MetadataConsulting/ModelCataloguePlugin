package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.AssetGormService

@CompileStatic
class AssetCatalogueElementService<T extends CatalogueElement> extends AbstractCatalogueElementService<Asset> {

    AssetGormService assetGormService

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(Asset.class.name)
    }

    @Override
    CatalogueElement findById(Long id) {
        assetGormService.findById(id)
    }
}
