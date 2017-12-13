package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.actions.Batch
import org.springframework.context.MessageSource

class BatchGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
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
