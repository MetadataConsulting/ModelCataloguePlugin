package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.MeasurementUnitGormService

@CompileStatic
class MeasurementUnitReorderInternalService extends AbstractReorderInternalService {
    MeasurementUnitGormService measurementUnitGormService

    @Override
    CatalogueElement findById(Long id) {
        measurementUnitGormService.findById(id)
    }
}
