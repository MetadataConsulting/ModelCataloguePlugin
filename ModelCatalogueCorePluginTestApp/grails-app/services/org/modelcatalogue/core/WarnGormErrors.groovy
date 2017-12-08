package org.modelcatalogue.core

import org.springframework.context.MessageSource

trait WarnGormErrors {

    void warnErrors(def bean, MessageSource messageSource, Locale locale = Locale.getDefault()) {
        if (!log.isWarnEnabled()) {
            return
        }

        StringBuilder message = new StringBuilder(
                "problem ${bean.id ? 'updating' : 'creating'} ${bean.getClass().simpleName}: $bean")
        for (fieldErrors in bean.errors) {
            for (error in fieldErrors.allErrors) {
                message.append("\n\t").append(messageSource.getMessage(error, locale))
            }
        }
        log.warn message
    }
}
