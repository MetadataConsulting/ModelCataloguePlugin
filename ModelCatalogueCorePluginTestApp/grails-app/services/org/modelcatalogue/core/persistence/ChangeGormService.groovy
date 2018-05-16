package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.audit.Change

class ChangeGormService {

    @Transactional
    Change findById(long id) {
        Change.get(id)
    }

    @Transactional(readOnly = true)
    List<Change> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<Change>
        }
        Change.where { id in ids }.list()
    }
}
