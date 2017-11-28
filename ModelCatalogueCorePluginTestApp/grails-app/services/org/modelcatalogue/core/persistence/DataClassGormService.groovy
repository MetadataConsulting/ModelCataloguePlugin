package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.DataClass

class DataClassGormService {

    @Transactional
    DataClass findById(long id) {
        DataClass.get(id)
    }
}
