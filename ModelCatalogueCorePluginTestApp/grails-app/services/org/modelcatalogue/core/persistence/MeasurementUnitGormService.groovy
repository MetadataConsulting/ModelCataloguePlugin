package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.MeasurementUnit
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class MeasurementUnitGormService {

    @Transactional
    MeasurementUnit findById(long id) {
        MeasurementUnit.get(id)
    }
}
