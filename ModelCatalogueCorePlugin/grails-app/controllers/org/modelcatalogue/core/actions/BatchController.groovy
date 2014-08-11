package org.modelcatalogue.core.actions

import org.modelcatalogue.core.AbstractRestfulController
import org.modelcatalogue.core.util.Lists
import org.springframework.http.HttpStatus

class BatchController extends AbstractRestfulController<Batch> {

    def actionService

    static allowedMethods = [index: 'GET', actions: 'GET', run: 'POST', reactivate: 'POST', dismiss: 'POST']

    @Override
    protected String getRoleForSaveAndEdit() {
        "ADMIN"
    }

    BatchController() {
        super(Batch)
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

        reportCapableRespond Lists.wrap(params, "/${resourceName}/${batch.id}/actions/${params.state ?: 'pending'}", "actions", actionService.list(params, batch, state))
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

        Action action = Action.lock(params.long('actionId'))

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

        Action action = Action.lock(params.long('actionId'))

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

        Action action = Action.lock(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        actionService.run(action)
        respond status: HttpStatus.OK
    }
}
