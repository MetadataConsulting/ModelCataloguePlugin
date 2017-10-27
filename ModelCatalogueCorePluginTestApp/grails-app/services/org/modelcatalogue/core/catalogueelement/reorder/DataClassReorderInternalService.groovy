package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.DataClassGormService

@CompileStatic
class DataClassReorderInternalService extends AbstractReorderInternalService {
    DataClassGormService dataClassGormService
    
    @Override
    CatalogueElement findById(Long id) {
        dataClassGormService.findById(id)
    }
}
