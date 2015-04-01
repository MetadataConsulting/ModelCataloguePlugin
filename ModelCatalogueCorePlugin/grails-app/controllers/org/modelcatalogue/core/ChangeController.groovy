package org.modelcatalogue.core

import grails.rest.RestfulController
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.util.Lists

import static org.springframework.http.HttpStatus.*

class ChangeController extends RestfulController<Change> {

    ChangeController() {
        super(Change)
    }

    static responseFormats = ['json']

    def auditService
    def classificationService

    def undo() {
        Change change = Change.get(params.id)

        if (!change) {
            render status: NOT_FOUND
            return
        }

        if (change.undo()) {
            render status: OK
            return
        }

        render status: NOT_ACCEPTABLE
    }

    def changes() {
        if (!params.max) {
            params.max = 10
        } else {
            params.max = params.long('max')
        }
        Change change = Change.get(params.id)

        if (!change) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/changes", auditService.getSubChanges(params, change))
    }

    def global() {
        if (!params.max) {
            params.max = 10
        } else {
            params.max = params.long('max')
        }

        respond Lists.wrap(params, "/change/", auditService.getGlobalChanges(params, classificationService.classificationsInUse))
    }


}
