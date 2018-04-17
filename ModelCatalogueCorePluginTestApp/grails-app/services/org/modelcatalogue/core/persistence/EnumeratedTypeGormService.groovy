package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchQuery
import org.springframework.context.MessageSource

class EnumeratedTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    EnumeratedType findById(Long id) {
        EnumeratedType.get(id)
    }

    @Transactional
    EnumeratedType saveWithStatusAndNameAndEnumerations(ElementStatus status, String name, Map enumerations) {
        save(new EnumeratedType(status: status, name: name, enumerations: enumerations))
    }

    @Transactional
    EnumeratedType save(EnumeratedType enumeratedTypeInstance) {
        if (!enumeratedTypeInstance.save()) {
            warnErrors(enumeratedTypeInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        enumeratedTypeInstance
    }

    @Transactional(readOnly = true)
    Number countByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        findQueryByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery).count()
    }

    @CompileStatic
    DetachedCriteria<EnumeratedType> findQueryByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        DetachedCriteria<EnumeratedType> query = EnumeratedType.where {}
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

    DetachedCriteria<EnumeratedType> queryByIds(List<Long> ids) {
        EnumeratedType.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<EnumeratedType> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<EnumeratedType>
        }
        queryByIds(ids).list()
    }
}

