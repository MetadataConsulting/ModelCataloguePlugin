package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataType

class DataTypeGormService {

    @Transactional(readOnly = true)
    DataType findByName(String name) {
        findQueryByName(name).get()
    }

    DetachedCriteria<DataType> findQueryByName(String nameParam) {
        DataType.where { name == nameParam }
    }
}
