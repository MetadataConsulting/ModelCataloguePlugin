package org.modelcatalogue.core.catalogueelement.addrelation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.DataClassGormService

@CompileStatic
class DataClassAddRelationService extends AbstractAddRelationService {

    DataClassGormService dataClassGormService

    @Override
    CatalogueElement findById(Long id) {
        dataClassGormService.findById(id)
    }
}
