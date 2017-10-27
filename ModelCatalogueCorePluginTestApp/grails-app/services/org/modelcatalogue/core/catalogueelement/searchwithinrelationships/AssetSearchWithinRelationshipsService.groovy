package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.AssetGormService

@CompileStatic
class AssetSearchWithinRelationshipsService extends  AbstractSearchWithinRelationshipsService {

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
