package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.persistence.DataModelGormService

@CompileStatic
class DataModelSearchWithinRelationshipsService extends AbstractSearchWithinRelationshipsService {

    DataModelGormService dataModelGormService

    @Override
    CatalogueElement findById(Long id) {
        dataModelGormService.findById(id)
    }

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(DataModel.class.name)
    }
}
