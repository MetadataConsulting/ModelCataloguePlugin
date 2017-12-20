package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.persistence.DataTypeGormService

@CompileStatic
class DataTypeCatalogueElementService<T extends CatalogueElement> extends AbstractCatalogueElementService<DataType> {

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
