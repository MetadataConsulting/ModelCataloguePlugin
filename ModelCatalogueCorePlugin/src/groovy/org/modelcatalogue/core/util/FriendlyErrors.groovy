package org.modelcatalogue.core.util

import grails.util.Holders
import org.springframework.context.MessageSource
import org.springframework.validation.Errors
import org.springframework.validation.ObjectError

/**
 * Created by ladin on 05.02.15.
 */
class FriendlyErrors {

    static String printErrors(String message, Errors errors) {
        MessageSource messageSource = Holders.applicationContext.getBean(MessageSource)
        Locale locale = Locale.UK
        StringBuilder builder = new StringBuilder(message)
        builder << ':\n'
        for (ObjectError error in errors.allErrors) {
            builder << '    ' << messageSource.getMessage(error, locale)
        }
        builder.toString()
    }

}
