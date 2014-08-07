package org.modelcatalogue.core.actions

import grails.gorm.DetachedCriteria
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.exceptions.DefaultStackTraceFilterer
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

@Log4j
class ActionService {

    ExecutorService executorService

    /**
     * Performs the action in the background thread. Follow the action state to see weather the action has been already
     * performed. If the action depends on any other actions they are performed prior executing this action but after
     * setting this action state to PERFORMING.
     * @param action action to be performed
     * @return future to the outcome of the action or the error message
     */
    Future<ActionResult> run(Action action, List<Long> executionStack = []) {
        List<Long> currentExecutionStack = new ArrayList<Long>(executionStack)
        currentExecutionStack << action.id

        if (action.id in executionStack) {
            Future<ActionResult> msg = new FutureTask<ActionResult>({new ActionResult(outcome: "Circular dependency found: ${currentExecutionStack.join(' -> ')}", failed: true)})
            msg.run()
            return msg
        }

        if (action.state != ActionState.PENDING) {
            Future<ActionResult> msg = new FutureTask<ActionResult>({new ActionResult(outcome: "The action is already pending", failed: action.state == ActionState.FAILED)})
            msg.run()
            return msg
        }

        ActionDependency failed = action.dependsOn.find { it.provider.state == ActionState.FAILED }
        if (failed) {
            String msgStart = 'An action on which this action depends failed with following error:'
            action.state = ActionState.FAILED
            action.outcome = failed.provider.outcome?.startsWith(msgStart) ? failed.provider.outcome : "$msgStart$failed.provider.outcome"
            action.save(failOnError: true)
            Future<ActionResult> msg = new FutureTask<ActionResult>({ new ActionResult(outcome: action.outcome, failed: true) })
            msg.run()
            return msg
        } else {
            action.state = ActionState.PERFORMING
            action.save(failOnError: true)
        }

        Map<Long, Future<ActionResult>> dependenciesPerformed = [:]

        for(ActionDependency dependency in action.dependsOn) {
            dependenciesPerformed.put dependency.provider.id, run(dependency.provider, currentExecutionStack)
        }

        Long id = action.id

        executorService.submit({
            try {
                Action a = Action.get(id)

                StringWriter sw = new StringWriter()
                PrintWriter pw = new PrintWriter(sw)

                ActionRunner runner = a.type.newInstance()
                runner.initWith(a.ext)
                runner.out = pw

                try {
                    // this will cause waiting for all provider actions to be completed before the execution
                    for (Map.Entry<Long, Future<ActionResult>> future in dependenciesPerformed) {
                        ActionResult result = future.value.get()
                        if (result.failed) {
                            String msgStart = 'Action failed because at least one of the dependencies failed. The error from the dependency follows:\n\n'
                            a.state = ActionState.FAILED
                            a.outcome = result.outcome?.startsWith(msgStart) ? result.outcome : "$msgStart$result.outcome"
                            a.save(failOnError: true)
                            return new ActionResult(outcome: a.outcome, failed: true)
                        }
                    }

                    runner.run()
                    if (runner.failed) {
                        a.state = ActionState.FAILED
                    } else {
                        a.state = ActionState.PERFORMED
                    }
                } catch (e) {
                    new DefaultStackTraceFilterer(true).filter(e)
                    e.printStackTrace(runner.out)
                    a.state = ActionState.FAILED
                }
                a.outcome = sw.toString()
                a.save(failOnError: true)
                return new ActionResult(outcome: a.outcome, failed: a.state == ActionState.FAILED)
            } catch (e) {
                new DefaultStackTraceFilterer(true).filter(e)
                String message = "Exception executing action $action.type with parameters $action.ext: ${e}"
                log.warn(message, e)
                return new ActionResult(outcome: message, failed: true)
            }

        })
    }

    void dismiss(Action action){
        if (action.state == ActionState.DISMISSED) {
            return
        }
        action.state = ActionState.DISMISSED
        action.save(failOnError: true)

        for (ActionDependency dependency in action.dependencies) {
            dismiss dependency.dependant
        }
    }

    Action create(Map<String, String> parameters = [:], Class<? extends ActionRunner> runner, Action... dependsOn) {
        Action created = new Action(type: runner)
        created.validate()

        if (created.hasErrors()) {
            return created
        }

        ActionRunner runnerInstance = runner.newInstance()
        Map<String, String> parameterErrors = runnerInstance.validate(parameters)

        parameterErrors.each { key, message ->
            created.errors.rejectValue('extensions', "${runner.name}.$key", message)
        }

        if (created.hasErrors()) {
            return created
        }

        created.save()

        if (parameters) {
            created.ext.putAll parameters
        }

        for (Action action in dependsOn) {
            ActionDependency dependency = new ActionDependency(dependant: created, provider: action)
            dependency.save(failOnError: true)
            created.addToDependsOn(dependency)
            action.addToDependencies(dependency)
        }

        created
    }

    ListWithTotalAndType<Action> list(Map params = [:]) {
        list(params, ActionState.PENDING)
    }

    ListWithTotalAndType<Action> list(Map params = [:], ActionState state) {
        Lists.fromCriteria(params, Action) {
            eq 'state', state
        }
    }

    /**
     * Searches for all actions with given type and search params (contained in ext/extensions map).
     * @param searchParams parameter and their expected values which should be present in ext/extensions map
     * @param type the action runner type to look for
     * @param queryParams query parameters such as offset, limit and so on
     * @return the list with total and type for given search parameters and type
     */
    ListWithTotalAndType<Action> listByTypeAndParams(Map<String, String> searchParams = [:], Class<? extends ActionRunner> type, ActionState state = ActionState.PENDING, Map queryParams = [:]) {
        if (!searchParams) {
            return Lists.fromCriteria(queryParams, Action) {
                eq 'type', type
                if (state) {
                    eq 'state', state
                }
            }
        }

        Set<Long> ids = null

        for (Map.Entry<String, String> parameter in searchParams.entrySet()) {
            DetachedCriteria<ActionParameter> parameters = ActionParameter.where {
                name == parameter.key && extensionValue == parameter.value
            }
            if (!parameters.count()) {
                return Lists.emptyListWithTotalAndType(Action)
            }
            if (ids == null) {
                ids = []
                parameters.each {
                    ids << it.action.id
                }
            } else {
                ids = ids.intersect(parameters.collect { it.action.id })
            }
        }

        Lists.fromCriteria(queryParams, Action) {
            inList 'id', ids
            eq 'type', type
            if (state) {
                eq 'state', state
            }
        }


    }

}
