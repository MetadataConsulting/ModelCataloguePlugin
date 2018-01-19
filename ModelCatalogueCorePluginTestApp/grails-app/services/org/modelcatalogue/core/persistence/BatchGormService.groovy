package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.actions.Batch
import org.springframework.context.MessageSource

class BatchGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    List<Batch> findAll() {
        Batch.where {}.list()
    }

    @Transactional
    void update(List<Long> batchIds, Boolean archived) {
        DetachedCriteria<Batch> query = Batch.where { id in batchIds }
        query.updateAll('archived': archived)
    }

    @Transactional(readOnly = true)
    List<Batch> findAllActive() {
        queryActive().list()
    }

    @Transactional(readOnly = true)
    Number countActive() {
        queryActive().count()
    }

    DetachedCriteria<Batch> queryActive() {
        Batch.where { archived == false }
    }

    @Transactional(readOnly = true)
    Batch findById(long id) {
        Batch.get(id)
    }

    @Transactional
    Batch saveWithName(String name) {
        save(new Batch(name: name))
    }

    @Transactional
    Batch save(Batch batchInstance) {
        if ( !batchInstance.save() ) {
            warnErrors(batchInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        batchInstance
    }


}
