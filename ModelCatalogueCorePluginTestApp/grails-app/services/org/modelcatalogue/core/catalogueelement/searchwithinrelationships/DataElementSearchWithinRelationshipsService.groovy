package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.persistence.DataElementGormService

@CompileStatic
class DataElementSearchWithinRelationshipsService extends AbstractSearchWithinRelationshipsService {

    DataElementGormService dataElementGormService

    @Override
    CatalogueElement findById(Long id) {
        dataElementGormService.findById(id)
    }

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(DataElement.class.name)
    }
}
