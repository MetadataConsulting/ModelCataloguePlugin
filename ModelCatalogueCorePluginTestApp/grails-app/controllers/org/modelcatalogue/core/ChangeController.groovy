package org.modelcatalogue.core

import static org.springframework.http.HttpStatus.*
import grails.rest.RestfulController
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.persistence.ChangeGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.Lists

class ChangeController extends RestfulController<Change> {

    ChangeController() {
        super(Change)
    }

    static responseFormats = ['json']

    def auditService
    def dataModelService
    DataModelGormService dataModelGormService
    ChangeGormService changeGormService

    def undo() {
        Change change = changeGormService.findById(params.long('id'))

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
        long changeId = params.long('id')
        Change change = changeGormService.findById(changeId)

        if (!change) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/${resourceName}/${changeId}/changes", auditService.getSubChanges(params, change))
    }

    def global() {
        if (!params.max) {
            params.max = 10
        } else {
            params.max = params.long('max')
        }

        respond Lists.wrap(params, "/change/", auditService.getGlobalChanges(params, overridableDataModelFilter))
    }

    def dataModelActivity(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        long dataModelId = params.long('id')
        DataModel element = dataModelGormService.findById(dataModelId)
        if (!element) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/dataModel/${dataModelId}/activity", auditService.getGlobalChanges(params, DataModelFilter.includes(element)))
    }

    def userActivity(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        long userId = params.long('id')
        User element = User.get(userId)
        if (!element) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/user/${userId}/activity", auditService.getChangesForUser(params, element))
    }

    protected DataModelFilter getOverridableDataModelFilter() {
        if (params.dataModel) {
            DataModel dataModel = dataModelGormService.findById(params.long('dataModel'))
            if (dataModel) {
                return DataModelFilter.includes(dataModel)
            }
        }
        dataModelService.dataModelFilter
    }

}
