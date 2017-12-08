package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.DataModelPolicy
import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.WarnGormErrors
import org.springframework.context.MessageSource

class DataModelPolicyGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
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

    @Transactional
    DataModelPolicy saveWithNameAndPolicyText(String name, String policyText) {
        save(new DataModelPolicy(name: name, policyText: policyText))
    }

    @Transactional
    DataModelPolicy save(DataModelPolicy dataModelPolicyInstance) {
        if ( !dataModelPolicyInstance.save() ) {
            warnErrors(dataModelPolicyInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        dataModelPolicyInstance
    }
}
