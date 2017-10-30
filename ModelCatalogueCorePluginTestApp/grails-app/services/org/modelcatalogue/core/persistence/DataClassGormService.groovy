package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.DataClass
import org.springframework.transaction.annotation.Transactional

class DataClassGormService {

    @Transactional
    DataClass findById(long id) {
        DataClass.get(id)
    }
}
