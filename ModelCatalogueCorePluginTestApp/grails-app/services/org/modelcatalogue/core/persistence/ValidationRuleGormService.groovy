package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchStatusQuery
import org.springframework.context.MessageSource

class ValidationRuleGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    ValidationRule findById(long id) {
        ValidationRule.get(id)
    }

    @Transactional
    ValidationRule saveWithNameAndDescriptionAndStatus(String name, String description, ElementStatus status) {
        ValidationRule validationRuleInstance = new ValidationRule(name: name, description: description, status: status)
        if ( !validationRuleInstance.save() ) {
            warnErrors(validationRuleInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        validationRuleInstance
    }

    @Transactional(readOnly = true)
    Number countBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        findQueryBySearchStatusQuery(searchStatusQuery).count()
    }

    @CompileStatic
    DetachedCriteria<ValidationRule> findQueryBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        DetachedCriteria<ValidationRule> query = ValidationRule.where {}
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
