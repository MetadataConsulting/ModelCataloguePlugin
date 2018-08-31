package org.modelcatalogue.core.validation

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

@CompileStatic
class ValidationErrorsJsonView {
    List<ValidationErrorJsonView> errors

    static ValidationErrorsJsonView of(Errors errors, MessageSource messageSource, Locale locale) {
        new ValidationErrorsJsonView(errors: errors.allErrors.collect { ObjectError error ->

            String message = messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), locale)
            ValidationErrorJsonView errorJsonView = new ValidationErrorJsonView(code: error.getCode(),
                    objectName: error.getObjectName(),
                    defaultMessage: error.getDefaultMessage(),
                    message: message)
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error
                errorJsonView.field = fieldError.getField()
                errorJsonView.rejectedValue = fieldError.getRejectedValue()
            }

            errorJsonView
        })
    }
}
