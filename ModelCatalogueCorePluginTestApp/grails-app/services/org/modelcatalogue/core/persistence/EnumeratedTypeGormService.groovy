package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchStatusQuery
import org.springframework.context.MessageSource

class EnumeratedTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    EnumeratedType findById(long id) {
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
    Number countBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        findQueryBySearchStatusQuery(searchStatusQuery).count()
    }

    @CompileStatic
    DetachedCriteria<EnumeratedType> findQueryBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        DetachedCriteria<EnumeratedType> query = EnumeratedType.where {}
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

