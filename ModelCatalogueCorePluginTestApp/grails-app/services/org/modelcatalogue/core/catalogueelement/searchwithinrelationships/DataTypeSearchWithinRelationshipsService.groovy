package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.persistence.DataTypeGormService

@CompileStatic
class DataTypeSearchWithinRelationshipsService extends AbstractSearchWithinRelationshipsService {

    DataTypeGormService dataTypeGormService

    @Override
    CatalogueElement findById(Long id) {
        dataTypeGormService.findById(id)
    }

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(DataType.class.name)
    }
}
