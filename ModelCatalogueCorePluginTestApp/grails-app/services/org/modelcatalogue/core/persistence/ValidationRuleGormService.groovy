package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
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
}
