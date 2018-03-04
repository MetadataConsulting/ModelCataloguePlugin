package org.modelcatalogue.core.util

import groovy.transform.CompileDynamic
import org.springframework.context.MessageSource

class BeanMessageUtils {

    @CompileDynamic
    static List<String> beanMessages(def bean, MessageSource messageSource, Locale locale = Locale.getDefault()) {
        List<String> errorMsgs = []
        for (fieldErrors in bean.errors) {
            for (error in fieldErrors.allErrors) {
                errorMsgs << messageSource.getMessage(error, locale)
            }
        }
        return errorMsgs
    }
}