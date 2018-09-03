package org.modelcatalogue.core.validation

import groovy.transform.CompileStatic

@CompileStatic
class ValidationErrorJsonView {

    String code
    String objectName
    String field
    Object rejectedValue
    String message
    String defaultMessage
}
