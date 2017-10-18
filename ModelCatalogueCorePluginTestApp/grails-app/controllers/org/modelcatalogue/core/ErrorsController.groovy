package org.modelcatalogue.core

import groovy.transform.CompileStatic
import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
@CompileStatic
class ErrorsController {

    def error403() {
        response.status = 403
        [:]
    }

    def error500() {
        render view: '/error'
    }
}
