package org.modelcatalogue.core.validation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.scripting.ValidatingImpl

@CompileStatic
class ValidationRulesJsonView {
    String gormUrl
    String name
    List<ValidationRuleJsonView> rules = []
    ValidatingImpl validating
}