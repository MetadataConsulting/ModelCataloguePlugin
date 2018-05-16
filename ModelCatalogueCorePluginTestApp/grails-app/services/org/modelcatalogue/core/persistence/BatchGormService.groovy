package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionState
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

    @Transactional(readOnly = true)
    List<Batch> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<Batch>
        }
        Batch.where { id in ids }.list()
    }

    @Transactional
    Batch saveWithName(String name) {
        save(new Batch(name: name))
    }

    @Transactional
    Batch save(Batch batchInstance) {
        if (!batchInstance.save()) {
            warnErrors(batchInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        batchInstance
    }

    @Transactional
    void removeActionsInStateFromBatch(Batch batch, List<ActionState> stateList) {
        for (Action action in new HashSet<Action>(batch.actions)) {
            if (action.state in stateList) {
                batch.removeFromActions(action)
                action.batch = null
                action.delete()
            }
        }
        batch.save()
    }

    @Transactional(readOnly = true)
    List<Batch> findAllByNameIlike(String name) {
        findQueryByNameIlike(name).list()
    }

    @Transactional(readOnly = true)
    Batch findByNameIlike(String name) {
        findQueryByNameIlike(name).get()
    }

    DetachedCriteria<Batch> findQueryByNameIlike(String batchName) {
        Batch.where {
            name =~ batchName
        }
    }
}