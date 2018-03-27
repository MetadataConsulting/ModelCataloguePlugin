package org.modelcatalogue.core.util

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class LoggedUserUtils {

    @CompileDynamic
    static Object id(Object principal) {
        if ( principal == null ) {
            return null
        }
        if ( principal instanceof String ) {
            try {
                return principal as Long
            } catch(NumberFormatException e) {
                return null
            }
        }
        if ( principal instanceof GrailsUser ) {
            return ((GrailsUser) principal).id
        }
        if ( principal.respondsTo('id') ) {
            return principal.id as Long
        }

        null
    }
}
