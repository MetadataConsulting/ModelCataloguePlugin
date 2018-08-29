package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchQuery
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
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
    Number countByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        findQueryByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery).count()
    }

    DetachedCriteria<ValidationRule> findQueryByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        DetachedCriteria<ValidationRule> query = ValidationRule.where {}
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

    DetachedCriteria<ValidationRule> queryByIds(List<Long> ids) {
        ValidationRule.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<ValidationRule> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<ValidationRule>
        }
        queryByIds(ids).list()
    }

    @Override
    Logger getLog() {
        return log
    }
}
