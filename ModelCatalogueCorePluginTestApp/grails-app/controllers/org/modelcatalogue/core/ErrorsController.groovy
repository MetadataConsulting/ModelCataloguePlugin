package org.modelcatalogue.core

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.model.NotFoundException
import groovy.transform.CompileStatic
import grails.plugin.springsecurity.annotation.Secured
import static org.springframework.http.HttpStatus.UNAUTHORIZED
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

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
        render status: UNAUTHORIZED
    }

    def error500() {
        render view: '/error', status: INTERNAL_SERVER_ERROR
    }
}
