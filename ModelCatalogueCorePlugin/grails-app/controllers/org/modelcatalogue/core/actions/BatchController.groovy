package org.modelcatalogue.core.actions

import org.modelcatalogue.core.AbstractRestfulController
import org.modelcatalogue.core.util.Lists
import org.springframework.http.HttpStatus

class BatchController extends AbstractRestfulController<Batch> {

    def actionService

    static allowedMethods = [index: 'GET', actions: 'GET', run: 'POST', reactivate: 'POST', dismiss: 'POST', updateActionParameters: 'PUT', addDependency: 'POST', removeDependency: 'DELETE']

    @Override
    protected String getRoleForSaveAndEdit() {
        "ADMIN"
    }

    BatchController() {
        super(Batch)
    }

    def archive() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

        if (!params.id) {
            notFound()
            return
        }

        Batch batch = Batch.get(params.id)

        if (!batch) {
            notFound()
            return
        }

        batch.archived = true

        respond batch
    }

    def runAll() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

        if (!params.id) {
            notFound()
            return
        }

        Batch batch = Batch.get(params.id)

        if (!batch) {
            notFound()
            return
        }

        for (Action action in batch.actions) {
            actionService.run(action)
        }

        respond batch
    }

    @Override
    def index(Integer max) {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
        handleParams(max)
        reportCapableRespond Lists.fromCriteria(params, resource, "/${resourceName}/") {
            eq 'archived', params.boolean('archived') || params.status == 'archived'
        }
    }


    def updateActionParameters() {

        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

        if (!params.actionId) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        if (action.state in [ActionState.PERFORMING, ActionState.PERFORMED]) {
            render status: HttpStatus.METHOD_NOT_ALLOWED
            return
        }
        actionService.updateParameters(action, request.getJSON())

        if (action.hasErrors()) {
            respond action.errors
            return
        }
        respond action
    }

    def listActions(Integer max) {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
        handleParams(max)

        if (!params.id) {
            notFound()
            return
        }

        Batch batch = Batch.get(params.long('id'))

        if (!batch) {
            notFound()
            return
        }

        ActionState state = params.state ? ActionState.valueOf(params.state.toString().toUpperCase()) : null

        reportCapableRespond Lists.wrap(params, "/${resourceName}/${batch.id}/actions/${params.state ?: ''}", "actions", actionService.list(params, batch, state))
    }

    def dismiss() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

        if (!params.actionId) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        actionService.dismiss(action)
        respond status: HttpStatus.OK
    }

    def reactivate(){
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
        if (!params.actionId) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        actionService.reactivate(action)
        respond status: HttpStatus.OK
    }


    def run(){
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

        if (!params.actionId) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        actionService.run(action)
        respond status: HttpStatus.OK
    }

    def addDependency() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

        if (!params.actionId || !params.providerId || !params.role) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }



        Action provider = Action.get(params.long('providerId'))

        if (!provider) {
            notFound()
            return
        }


        ActionDependency dependency = actionService.addDependency(action, provider, params.role.toString())

        if (!dependency) {
            respond status: HttpStatus.METHOD_NOT_ALLOWED
        }

        respond action
    }

    def removeDependency() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

        if (!params.actionId || !params.role) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }


        ActionDependency dependency = actionService.removeDependency(action, params.role.toString())

        if (!dependency) {
            notFound()
            return
        }

        respond action
    }
}
