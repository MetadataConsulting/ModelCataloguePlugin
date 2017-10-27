package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.persistence.DataClassGormService

@CompileStatic
class DataClassSearchWithinRelationshipsService extends AbstractSearchWithinRelationshipsService {

    DataClassGormService dataClassGormService

    @Override
    CatalogueElement findById(Long id) {
        dataClassGormService.findById(id)
    }


    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(DataClass.class.name)
    }
}
