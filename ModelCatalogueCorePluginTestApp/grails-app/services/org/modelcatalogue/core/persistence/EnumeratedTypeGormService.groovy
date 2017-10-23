package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.EnumeratedType

class EnumeratedTypeGormService {

    @Transactional
    EnumeratedType findById(long id) {
        EnumeratedType.get(id)
    }
}
