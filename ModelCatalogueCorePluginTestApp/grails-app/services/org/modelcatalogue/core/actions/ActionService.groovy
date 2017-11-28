package org.modelcatalogue.core.actions

import grails.gorm.DetachedCriteria
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.exceptions.DefaultStackTraceFilterer
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

@Log4j
class ActionService {

    ExecutorService executorService
    @Autowired AutowireCapableBeanFactory autowireBeanFactory
    @Autowired SecurityService modelCatalogueSecurityService
    @Autowired AuditService auditService


    /**
     * Performs the action in the background thread. Follow the action state to see weather the action has been already
     * performed. If the action depends on any other actions they are performed prior executing this action but after
     * setting this action state to PERFORMING.
     * @param action action to be performed
     * @return future to the outcome of the action or the error message
     */
    Future<ActionResult> run(Action action) {
        runInternal(action, true, false, [])
    }

    private Future<ActionResult> runInternal(Action action, boolean async, boolean ignorePerforming, List<Long> executionStack) {
        List<Long> currentExecutionStack = new ArrayList<Long>(executionStack)
        currentExecutionStack << action.id

        if (action.id in executionStack) {
            Future<ActionResult> msg = new FutureTask<ActionResult>({new ActionResult(outcome: "Circular dependency found: ${currentExecutionStack.join(' -> ')}", failed: true)})
            msg.run()
            return msg
        }

        if (!(ignorePerforming && action.state == ActionState.PERFORMING) && action.state != ActionState.PENDING) {
            Future<ActionResult> msg = new FutureTask<ActionResult>({new ActionResult(outcome: "The action is not pending", failed: action.state == ActionState.FAILED, result: action.result)})
            msg.run()
            return msg
        }

        ActionDependency failed = action.dependsOn.find { it.provider.state == ActionState.FAILED }
        if (failed) {
            String msgStart = 'An action on which this action depends failed with following error:'
            action.state = ActionState.FAILED
            action.outcome = failed.provider.outcome?.startsWith(msgStart) ? failed.provider.outcome : "$msgStart$failed.provider.outcome"
            action.save(failOnError: true, flush: true)
            Future<ActionResult> msg = new FutureTask<ActionResult>({ new ActionResult(outcome: action.outcome, failed: true) })
            msg.run()
            return msg
        } else {
            action.state = ActionState.PERFORMING
            action.save(failOnError: true, flush: true)
        }

        Long id = action.id

        Closure<ActionResult> job = {
            try {
                Action a = Action.get(id)
                
                StringWriter sw = new StringWriter()
                PrintWriter pw = new PrintWriter(sw)

                ActionRunner runner = createRunner(a.type)
                runner.out = pw

                try {
                    // first set all deps as pending
                    for(ActionDependency dependency in a.dependsOn) {
                        if (dependency.provider.state == ActionState.PENDING){
                            dependency.provider.state = ActionState.PERFORMING
                            dependency.provider.save(failOnError: true, flush: true)
                        }
                    }

                    Map<String, String> parameters = [:]

                    // than actually run, but ignoring the pending check
                    for(ActionDependency dependency in a.dependsOn) {
                        ActionResult result = runInternal(dependency.provider, false, true, currentExecutionStack).get()
                        if (result.failed) {
                            String msgStart = 'Action failed because at least one of the dependencies failed. The error from the dependency follows:\n\n'
                            a.state = ActionState.FAILED
                            a.outcome = result.outcome?.startsWith(msgStart) ? result.outcome : "$msgStart$result.outcome"
                            a.save(failOnError: true, flush: true)
                            return new ActionResult(outcome: a.outcome, failed: true)
                        } else {
                            parameters[dependency.role] = result.result
                        }
                    }

                    parameters.putAll a.ext

                    runner.initWith(parameters)

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
                a.result = runner.result
                a.save(failOnError: true, flush: true)
                return new ActionResult(outcome: a.outcome, failed: a.state == ActionState.FAILED, result: runner.result)
            } catch (e) {
                new DefaultStackTraceFilterer(true).filter(e)
                String message = "Exception executing action $action.type with parameters $action.ext: ${e}"
                log.warn(message, e)
                return new ActionResult(outcome: message, failed: true)
            }

        }
        if (async) {
            Long authorId = modelCatalogueSecurityService.currentUser?.id
            return executorService.submit({ auditService.withDefaultAuthorId(authorId, job) } as Callable<ActionResult>)
        }
        FutureTask<ActionResult> task = new FutureTask(job)
        task.run()
        task
    }

    void dismiss(Action action){
        if (action.state == ActionState.DISMISSED) {
            return
        }
        action.state = ActionState.DISMISSED
        action.save(failOnError: true, flush: true)

        for (ActionDependency dependency in action.dependencies) {
            dismiss dependency.dependant
        }
    }

    void reactivate(Action action){
        if (action.state == ActionState.PENDING) {
            return
        }
        action.state = ActionState.PENDING
        action.save(failOnError: true, flush: true)

        for (ActionDependency provider in action.dependsOn) {
            reactivate provider.provider
        }
    }

    Action updateParameters(Action action, Map<String, String> parameters) {
        ActionRunner runnerInstance = createRunner(action.type)
        Map<String, String> parameterErrors = runnerInstance.validate(parameters.collectEntries {key, value -> [key, value?.toString()]} as Map<String, String>)

        parameterErrors.each { key, message ->
            action.errors.rejectValue('extensions', "${action.type.name}.$key", message)
        }

        if (action.hasErrors()) {
            return action
        }

        action.ext.clear()
        action.ext.putAll parameters

        action
    }

    /**
     * Creates new action.
     *
     * If you pass actions as parameters values the become dependencies with role given by the key
     * in the parameters map.
     *
     * @param parameters
     * @param batch
     * @param runner
     * @return new action
     */
    Action create(Map<String, Object> parameters = [:], Batch batch, Class<? extends ActionRunner> runner) {
        Action created = new Action(type: runner, batch: batch)
        created.validate()

        if (created.hasErrors()) {
            return created
        }

        ActionRunner runnerInstance = createRunner(runner)
        Map<String, String> parameterErrors = runnerInstance.validate(parameters.findAll{ key, value -> !(value instanceof Action)}.collectEntries {key, value ->
            [key, value?.hasProperty('id') ? AbstractActionRunner.encodeEntity(value) : value?.toString()]
        } as Map<String, String>)

        parameterErrors.each { key, message ->
            created.errors.rejectValue('extensions', "${runner.name}.$key", message)
        }

        if (created.hasErrors()) {
            return created
        }

        Map<String, String> extensionParameters = [:]

        if (parameters) {
            parameters.each { key, value ->
                if (value instanceof Action) {
                    return
                }
                if (value.hasProperty('id')) {
                    extensionParameters.put(key, AbstractActionRunner.encodeEntity(value))
                } else {
                    extensionParameters.put(key, value?.toString())
                }
            }
        }

        ListWithTotalAndType<Action> existing = listByTypeAndParams(extensionParameters, batch, runner)

        if (existing.total > 0) {
            Map<String, Action> deps = parameters.findAll{ key, value -> value instanceof Action }
            outer:
            for (Action old in existing.items) {
                if (!old.dependsOn && deps || old.dependsOn && old.dependsOn.size() != deps.size()) {
                    continue
                }
                for (ActionDependency dependency in old.dependsOn) {
                    if (deps[dependency.role] != dependency.provider) {
                        continue outer
                    }
                }
                return old
            }
        }

        FriendlyErrors.failFriendlySave(created)

        if (parameters) {
            extensionParameters.each { key, value ->
                created.addExtension(key, value)
            }
        }

        batch.addToActions(created)
        FriendlyErrors.failFriendlySave(batch)

        parameters.findAll{ key, value -> value instanceof Action }.each { String role, Action action ->
            ActionDependency dependency = new ActionDependency(dependant: created, provider: action, role: role)
            FriendlyErrors.failFriendlySave(dependency)
            created.addToDependsOn(dependency)
            FriendlyErrors.failFriendlySave(created)
            action.addToDependencies(dependency)
            FriendlyErrors.failFriendlySave(action)
        }

        created
    }

    ActionDependency addDependency(Action dependant, Action provider, String role) {
        if (dependant.state in [ActionState.PERFORMED, ActionState.PERFORMING]) {
            return null
        }
        ActionDependency dependency = new ActionDependency(dependant: dependant, provider: provider, role: role)
        dependency.save(failOnError: true, flush: true)
        dependant.addToDependsOn(dependency)
        provider.addToDependencies(dependency)
        dependency
    }

    ActionDependency removeDependency(Action dependant, String role) {
        ActionDependency dependency = ActionDependency.findByDependantAndRole(dependant, role)
        if (!dependency || dependant.state in [ActionState.PERFORMED, ActionState.PERFORMING]) {
            return null
        }
        dependant.removeFromDependsOn(dependency)
        dependency.provider.removeFromDependencies(dependency)
        dependency.delete(flush: true)
        dependency
    }

    ListWithTotalAndType<Action> list(Map params = [:], Batch batch) {
        list(params, batch, null)
    }

    ListWithTotalAndType<Action> list(Map params = [:], Batch batch, ActionState state) {
        Lists.fromCriteria(params, Action) {
            if (state) {
                eq 'state', state
            }
            eq 'batch', batch
            sort 'lastUpdated', 'asc'
        }
    }

    ListWithTotalAndType<Action> listActive(Map params = [:], Batch batch) {
        Lists.fromCriteria(params, Action) {
            ne 'state', ActionState.DISMISSED
            eq 'batch', batch
            sort 'lastUpdated', 'asc'
        }
    }

    int resetAllRunningActions() {
        // TODO: create test
        def criteria = new DetachedCriteria(Action).build {
            eq 'state', ActionState.PERFORMING
        }
        criteria.updateAll(state: ActionState.PENDING)
    }

    /**
     * Searches for all actions with given type and search params (contained in ext/extensions map).
     * @param searchParams parameter and their expected values which should be present in ext/extensions map
     * @param type the action runner type to look for
     * @param queryParams query parameters such as offset, limit and so on
     * @return the list with total and type for given search parameters and type
     */
    ListWithTotalAndType<Action> listByTypeAndParams(Map<String, String> searchParams = [:], Batch batch, Class<? extends ActionRunner> type, ActionState state = ActionState.PENDING, Map queryParams = [:]) {
        if (!searchParams) {
            return Lists.fromCriteria(queryParams, Action) {
                eq 'type', type
                eq 'batch', batch
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

        if (!ids) {
            return Lists.emptyListWithTotalAndType(Action)
        }

        Lists.fromCriteria(queryParams, Action) {
            inList 'id', ids
            eq 'type', type
            eq 'batch', batch
            if (state) {
                eq 'state', state
            }
        }


    }

    public <T extends ActionRunner> T createRunner(Class<T> type) {
        T runner = type.newInstance()
        autowireBeanFactory.autowireBean(runner)
        runner
    }

}
