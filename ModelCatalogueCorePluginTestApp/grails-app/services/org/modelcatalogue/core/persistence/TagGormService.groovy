package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.Tag

class TagGormService {

    @Transactional
    Tag findById(long id) {
        Tag.get(id)
    }
}
