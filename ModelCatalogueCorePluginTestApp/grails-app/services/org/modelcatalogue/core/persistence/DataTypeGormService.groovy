package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.DataType
import org.springframework.transaction.annotation.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class DataTypeGormService {

    @Transactional
    DataType findById(long id) {
        DataType.get(id)
    }
}
