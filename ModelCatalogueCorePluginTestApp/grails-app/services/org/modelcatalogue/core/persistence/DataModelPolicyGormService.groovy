package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModelPolicy
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class DataModelPolicyGormService {

    @Transactional
    DataModelPolicy findById(long id) {
        DataModelPolicy.get(id)
    }
}
