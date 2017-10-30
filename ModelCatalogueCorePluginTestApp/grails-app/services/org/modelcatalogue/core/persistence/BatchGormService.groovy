package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.actions.Batch
import org.springframework.transaction.annotation.Transactional

class BatchGormService {

    @Transactional
    Batch findById(long id) {
        Batch.get(id)
    }
}
