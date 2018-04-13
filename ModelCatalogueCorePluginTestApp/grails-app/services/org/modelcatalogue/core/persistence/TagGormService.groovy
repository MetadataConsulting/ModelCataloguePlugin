package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.dashboard.SearchQuery
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


    @Transactional(readOnly = true)
    Number countByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        findQueryByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery).count()
    }

    DetachedCriteria<Tag> findQueryByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        DetachedCriteria<Tag> query = Tag.where {}
        if ( dataModelId ) {
            query = query.where { dataModel == DataModel.load(dataModelId) }
        }
        if ( searchStatusQuery.statusList ) {
            query = query.where { status in searchStatusQuery.statusList }
        }
        if ( searchStatusQuery.search ) {
            String term = "%${searchStatusQuery.search}%".toString()
            query = query.where { name =~ term }
        }
        query
    }
}
