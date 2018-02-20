package org.modelcatalogue.core.validation
import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class ValidationRuleJsonView {
    String name
    Map<String, String> identifiersToGormUrls
    String rule
}
