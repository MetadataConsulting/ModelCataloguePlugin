package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionRunner
import org.modelcatalogue.core.actions.Batch
import org.springframework.context.MessageSource

class ActionGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    Action saveWithBatchAndType(Batch batch, Class<? extends ActionRunner> type) {
        Action actionInstance = new Action(batch: batch, type: type)
        if ( !actionInstance.save() ) {
            warnErrors(actionInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        actionInstance
    }
}
