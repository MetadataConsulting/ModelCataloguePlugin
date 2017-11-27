package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.RelationshipType
import org.springframework.transaction.annotation.Transactional

class RelationshipTypeGormService {

    @Transactional
    RelationshipType findById(long id) {
        RelationshipType.get(id)
    }
}
