package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.RelationshipType
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class RelationshipTypeGormService {

    @Transactional
    RelationshipType findById(long id) {
        RelationshipType.get(id)
    }
}
