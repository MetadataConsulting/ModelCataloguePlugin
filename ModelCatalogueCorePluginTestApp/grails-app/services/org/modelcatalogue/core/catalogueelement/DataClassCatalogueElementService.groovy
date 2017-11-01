package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.persistence.DataClassGormService

@CompileStatic
class DataClassCatalogueElementService extends AbstractCatalogueElementService {

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
