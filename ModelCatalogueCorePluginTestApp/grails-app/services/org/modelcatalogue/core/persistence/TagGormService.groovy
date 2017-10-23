package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.Tag
import org.springframework.transaction.annotation.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class TagGormService {

    @Transactional
    Tag findById(long id) {
        Tag.get(id)
    }
}
