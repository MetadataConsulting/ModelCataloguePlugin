package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataClass
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class DataClassGormService {

    @Transactional
    DataClass findById(long id) {
        DataClass.get(id)
    }
}
