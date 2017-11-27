package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.DataModelPolicy
import grails.gorm.DetachedCriteria

class DataModelPolicyGormService {

    @Transactional
    DataModelPolicy findById(long id) {
        DataModelPolicy.get(id)
    }

    @Transactional(readOnly = true)
    DataModelPolicy findByName(String name) {
        findQueryByName(name).get()
    }

    protected DetachedCriteria<DataModelPolicy> findQueryByName(String nameParam) {
        DataModelPolicy.where { name == nameParam }
    }
}
