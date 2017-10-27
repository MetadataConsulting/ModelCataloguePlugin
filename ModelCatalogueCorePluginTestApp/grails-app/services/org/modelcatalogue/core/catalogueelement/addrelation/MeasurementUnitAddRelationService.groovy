package org.modelcatalogue.core.catalogueelement.addrelation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence. MeasurementUnitGormService

@CompileStatic
class  MeasurementUnitAddRelationService extends AbstractAddRelationService {

     MeasurementUnitGormService  measurementUnitGormService

    @Override
    CatalogueElement findById(Long id) {
         measurementUnitGormService.findById(id)
    }
}
