package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.audit.Change

class ChangeGormService {

    @Transactional
    Change findById(long id) {
        Change.get(id)
    }
}
