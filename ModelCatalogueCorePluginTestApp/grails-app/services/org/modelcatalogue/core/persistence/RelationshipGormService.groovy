package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.Relationship
import org.springframework.transaction.annotation.Transactional

class RelationshipGormService {

    @Transactional
    Relationship findById(long id) {
        Relationship.get(id)
    }
}

