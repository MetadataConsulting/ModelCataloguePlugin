package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.ValidationRule

@CompileStatic
class ValidationRuleGormService {

    @Transactional
    ValidationRule findById(long id) {
        ValidationRule.get(id)
    }
}
