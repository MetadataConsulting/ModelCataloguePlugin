package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.DataElementGormService

@CompileStatic
class DataElementReorderInternalService extends AbstractReorderInternalService {
    DataElementGormService dataElementGormService
    
    @Override
    CatalogueElement findById(Long id) {
        dataElementGormService.findById(id)
    }
}
