package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.MeasurementUnit

class MeasurementUnitGormService {

    @Transactional
    MeasurementUnit findById(long id) {
        MeasurementUnit.get(id)
    }
    @Transactional(readOnly = true)
    MeasurementUnit findByName(String name) {
        findQueryByName(name).get()
    }

    DetachedCriteria<MeasurementUnit> findQueryByName(String nameParam) {
        MeasurementUnit.where { name == nameParam }
    }
}
