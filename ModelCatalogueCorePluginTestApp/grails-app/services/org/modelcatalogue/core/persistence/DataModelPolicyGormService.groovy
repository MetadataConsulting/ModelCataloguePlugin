package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.DataModelPolicy
import org.springframework.transaction.annotation.Transactional

class DataModelPolicyGormService {

    @Transactional
    DataModelPolicy findById(long id) {
        DataModelPolicy.get(id)
    }
}
