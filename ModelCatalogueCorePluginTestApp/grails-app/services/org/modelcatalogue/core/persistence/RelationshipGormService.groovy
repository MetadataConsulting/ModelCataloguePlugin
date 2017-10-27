package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.Relationship
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class RelationshipGormService {

    @Transactional
    Relationship findById(long id) {
        Relationship.get(id)
    }
}

