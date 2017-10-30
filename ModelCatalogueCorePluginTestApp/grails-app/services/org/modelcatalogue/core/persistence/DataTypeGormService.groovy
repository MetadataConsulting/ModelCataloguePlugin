package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.DataType
import org.springframework.transaction.annotation.Transactional

class DataTypeGormService {

    @Transactional
    DataType findById(long id) {
        DataType.get(id)
    }
}
