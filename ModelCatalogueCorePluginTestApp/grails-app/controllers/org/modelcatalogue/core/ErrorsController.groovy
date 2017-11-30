package org.modelcatalogue.core

import static org.springframework.http.HttpStatus.UNAUTHORIZED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import groovy.transform.CompileStatic
import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
@CompileStatic
class ErrorsController {

    def error403() {
        render status: UNAUTHORIZED
    }

    def handleAccessDeniedException() {
        render status: UNAUTHORIZED
    }

    def handleNotFoundException() {
        render status: NOT_FOUND
    }

    def error500() {
        render view: '/error', status: INTERNAL_SERVER_ERROR
    }
}
