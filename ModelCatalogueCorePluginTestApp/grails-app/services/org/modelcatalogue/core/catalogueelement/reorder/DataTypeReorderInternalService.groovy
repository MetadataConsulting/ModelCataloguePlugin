package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.DataTypeGormService

@CompileStatic
class DataTypeReorderInternalService extends AbstractReorderInternalService {
    DataTypeGormService dataTypeGormService
    
    @Override
    CatalogueElement findById(Long id) {
        dataTypeGormService.findById(id)
    }
}
