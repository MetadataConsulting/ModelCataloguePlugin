package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.MeasurementUnit
import org.springframework.transaction.annotation.Transactional

class MeasurementUnitGormService {

    @Transactional
    MeasurementUnit findById(long id) {
        MeasurementUnit.get(id)
    }
}
