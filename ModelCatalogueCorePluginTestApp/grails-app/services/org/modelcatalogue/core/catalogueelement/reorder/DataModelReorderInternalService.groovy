package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.DataModelGormService

@CompileStatic
class DataModelReorderInternalService extends AbstractReorderInternalService {
    DataModelGormService dataModelGormService

    @Override
    CatalogueElement findById(Long id) {
        dataModelGormService.findById(id)
    }
}
