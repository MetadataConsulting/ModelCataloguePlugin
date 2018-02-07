package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionRunner
import org.modelcatalogue.core.actions.ActionState
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

    @Transactional(readOnly = true)
    Number countByBatch(Batch batch) {
        queryByBatch(batch).count()
    }

    @Transactional(readOnly = true)
    Number countByBatchAndState(Batch batchParam, ActionState stateParam) {
        Action.where {
            batch == batchParam && state == stateParam
        }.count()
    }

    @Transactional(readOnly = true)
    List<Action> findAllByBatch(Batch batchParam, List<ActionState> stateList, Integer offset, Integer max) {
        DetachedCriteria<Action> query = Action.where {
            batch == batchParam && state in stateList
        }
        query = query.sort('lastUpdated', 'asc')

        if ( offset != null && max != null ) {
            return query.list([max: max, offset: offset])

        }
        query.list()
    }

    @Transactional(readOnly = true)
    List<Action> findAllByIds(List<Long> ids) {
        Action.where { id in ids }.list()
    }

    protected DetachedCriteria<Action> queryByBatch(Batch batchParam) {
        Action.where {
            batch == batchParam
        }
    }

    @Transactional(readOnly = true)
    List<Action> findAllByBatchId(Long batchId) {
        findAllQueryByBatchId(batchId).list()
    }

    protected DetachedCriteria<Action> findAllQueryByBatchId(Long batchId) {
        Action.where {
            batch.id == batchId
        }
    }
}
