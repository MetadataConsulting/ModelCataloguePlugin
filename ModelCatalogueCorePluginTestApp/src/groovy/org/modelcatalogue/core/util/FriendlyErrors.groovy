package org.modelcatalogue.core.util

import grails.util.GrailsNameUtils
import grails.util.Holders
import grails.validation.ValidationException
import org.springframework.context.MessageSource
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

class FriendlyErrors {

    static String printErrors(String message, Errors errors) {
        MessageSource messageSource = Holders.applicationContext.getBean(MessageSource)
        Locale locale = Locale.UK
        StringBuilder builder = new StringBuilder(message)
        builder << ':\n'
        Set<String> messages = []
        for (ObjectError error in errors.allErrors) {
            if (error instanceof FieldError) {
                messages << "${messageSource.getMessage(error, locale)} (${GrailsNameUtils.getNaturalName(error.field)})"
            } else {
                messages << "${messageSource.getMessage(error, locale)}"
            }
        }
        for (String msg in messages) {
            builder << '    ' << msg << '\n'
        }
        builder.toString()
    }

    static <T> T withFriendlyFailure(String message = "Exception while saving element", Class<? extends RuntimeException> exceptionType = IllegalStateException, Closure<T> closure) {
        try {
            return closure()
        } catch(ValidationException ve) {
            throw exceptionType.newInstance(printErrors(message, ve.errors))
        }
    }

    static <T> T failFriendlySave(T object, String message = "Exception while saving element", Class<? extends RuntimeException> exceptionType = IllegalStateException) {
        if (!object.validate(deepValidate: false)) {
            throw exceptionType.newInstance(printErrors(message, object.errors))
        }
        object.save(failOnError: true, flush: true, validate: false)
    }

    static <T> T failFriendlySaveWithoutFlush(T object, String message = "Exception while saving element", Class<? extends RuntimeException> exceptionType = IllegalStateException) {
        if (!object.validate(deepValidate: false)) {
            throw exceptionType.newInstance(printErrors(message, object.errors))
        }
        object.save(failOnError: true, validate: false)
    }



}
