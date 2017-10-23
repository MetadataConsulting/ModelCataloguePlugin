package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.Batch
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class BatchGormService {

    @Transactional
    Batch findById(long id) {
        Batch.get(id)
    }
}
