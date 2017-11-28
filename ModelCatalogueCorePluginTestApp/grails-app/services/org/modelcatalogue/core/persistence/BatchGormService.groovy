package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.actions.Batch

class BatchGormService {

    @Transactional
    Batch findById(long id) {
        Batch.get(id)
    }
}
