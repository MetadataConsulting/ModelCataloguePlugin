package org.modelcatalogue.core.validation

import groovy.transform.CompileStatic

@CompileStatic
class ValidationRules {

    String gormUrl
    String name

    List<ValidationRuleJsonView> rules = []
}