package org.modelcatalogue.core.catalogueelement.addrelation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.DataElementGormService

@CompileStatic
class DataElementAddRelationService extends AbstractAddRelationService {

    DataElementGormService dataElementGormService

    @Override
    CatalogueElement findById(Long id) {
        dataElementGormService.findById(id)
    }
}
