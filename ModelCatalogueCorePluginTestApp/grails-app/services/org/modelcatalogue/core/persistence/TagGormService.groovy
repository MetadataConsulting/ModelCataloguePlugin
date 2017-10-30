package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.Tag
import org.springframework.transaction.annotation.Transactional

class TagGormService {

    @Transactional
    Tag findById(long id) {
        Tag.get(id)
    }
}
