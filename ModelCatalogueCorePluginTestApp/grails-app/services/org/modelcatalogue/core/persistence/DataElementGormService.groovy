package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.DataElement
import org.springframework.transaction.annotation.Transactional

class DataElementGormService {

    @Transactional
    DataElement findById(long id) {
        DataElement.get(id)
    }
}
