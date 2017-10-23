package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.ValidationRule
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class ValidationRuleGormService {

    @Transactional
    ValidationRule findById(long id) {
        ValidationRule.get(id)
    }
}
