package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.RelationshipType

class RelationshipTypeGormService {

    @Transactional
    RelationshipType findById(long id) {
        RelationshipType.get(id)
    }
}
