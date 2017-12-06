package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.WarnGormErrors
import org.springframework.context.MessageSource

class TagGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    Tag findById(long id) {
        Tag.get(id)
    }

    @Transactional
    Tag saveWithName(String name) {
        save(new Tag(name: name))
    }

    @Transactional
    Tag save(Tag tag) {
        if (!tag.save()) {
            warnErrors(tag, messageSource)
            transactionStatus.setRollbackOnly()
        }
        tag
    }
}
