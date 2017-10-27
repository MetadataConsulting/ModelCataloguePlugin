package org.modelcatalogue.core.catalogueelement.addrelation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.DataModelGormService

@CompileStatic
class DataModelAddRelationService extends AbstractAddRelationService {

    DataModelGormService dataModelGormService

    @Override
    CatalogueElement findById(Long id) {
        dataModelGormService.findById(id)
    }
}
