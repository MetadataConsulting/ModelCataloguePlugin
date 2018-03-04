package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.hibernate.transform.Transformers
import org.modelcatalogue.core.DataModelPolicy
import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.util.IdName
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

    @Transactional(readOnly = true)
    List<DataModelPolicy> findAllByIds(List<Long> ids) {
        DataModelPolicy.where {
            id in ids
        }.list()
    }

    @Transactional(readOnly = true)
   @CompileDynamic
    List<IdName> findAll() {
        BuildableCriteria c = DataModelPolicy.createCriteria()
        c.list {
            resultTransformer(Transformers.aliasToBean(IdName))
            projections {
                property('id', 'id')
                property('name', 'name')
            }
        }
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
