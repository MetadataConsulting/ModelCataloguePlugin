package org.modelcatalogue.core.actions

import org.modelcatalogue.core.AbstractRestfulController
import org.modelcatalogue.core.persistence.BatchGormService
import org.modelcatalogue.core.util.lists.Lists

class BatchController extends AbstractRestfulController<Batch> {

    def actionService
    BatchGormService batchGormService
    static allowedMethods = [index: 'GET', actions: 'GET', run: 'POST', reactivate: 'POST', dismiss: 'POST', updateActionParameters: 'PUT', addDependency: 'POST', removeDependency: 'DELETE']

    BatchController() {
        super(Batch)
    }

    def archive() {

        if (!params.id) {
            notFound()
            return
        }

        Batch batch = findById(params.long('id'))

        if (!batch) {
            notFound()
            return
        }

        batch.archived = true

        respond batch
    }

    def runAll() {

        if (!params.id) {
            notFound()
            return
        }

        Batch batch = findById(params.long('id'))

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

        handleParams(max)
        respond Lists.fromCriteria(params, resource, "/${resourceName}/") {
            eq 'archived', params.boolean('archived') || params.status == 'archived'
        }
    }

    def updateActionParameters() {


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
            methodNotAllowed()
            return
        }
        actionService.updateParameters(action, request.getJSON() as Map<String, String>)

        if (action.hasErrors()) {
            respond action.errors
            return
        }
        respond action
    }

    def listActions(Integer max) {

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

        respond Lists.wrap(params, "/${resourceName}/${batch.id}/actions/${params?.state ?: ''}", actionService.list(params, batch, state))
    }

    def dismiss() {


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
        ok()
    }

    def reactivate() {

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
        ok()
    }

    def run() {

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
        ok()
    }

    def addDependency() {

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
            methodNotAllowed()
        }

        respond action
    }

    def removeDependency() {

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

    protected Batch findById(long id) {
        batchGormService.findById(id)
    }
}
