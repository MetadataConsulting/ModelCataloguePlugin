package org.modelcatalogue.core.actions

import groovy.util.logging.Log4j
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
     * performed.
     * @param action action to be performed
     * @return future to the outcome of the action or the error message
     */
    Future<String> run(Action action) {
        if (action.state != ActionState.PENDING) {
            Future<String> msg = new FutureTask<String>({"The action is already pending"})
            msg.run()
            return msg
        }

        action.state = ActionState.PERFORMING
        action.save(failOnError: true)

        for(ActionDependency dependency in action.dependsOn) {
            run dependency.provider
        }

        Long id = action.id

        ActionRunner runner = action.actionClass.newInstance()
        runner.initWith(action.parameters)

        executorService.submit({
            try {
                Action a = Action.get(id)
                StringWriter sw = new StringWriter()
                PrintWriter pw = new PrintWriter(sw)
                try {
                    runner.out = pw
                    runner.run()
                    a.state = ActionState.PERFORMED
                } catch (e) {
                    a.state = ActionState.FAILED
                    e.printStackTrace(runner.out)
                }
                a.outcome = sw.toString()
                a.save(failOnError: true)
                return a.outcome
            } catch (e) {
                String message = "Exception executing action $action.actionClass with parameters $action.parameters: ${e}"
                log.warn(message, e)
                return message
            }

        })
    }

    void dismiss(Action action){
        if (action.state == ActionState.DISMISSED) {
            return
        }
        action.state = ActionState.DISMISSED
        action.save(failOnError: true)
    }

    Action create(Map<String, String> parameters = [:], Class<? extends ActionRunner> runner, Action... dependsOn) {
        Action created = new Action(parameters: new LinkedHashMap<String, String>(parameters), actionClass: runner)
        created.save()

        if (created.hasErrors()) {
            return created
        }

        ActionRunner runnerInstance = runner.newInstance()
        Map<String, String> parameterErrors = runnerInstance.validate(parameters)

        parameterErrors.each { key, message ->
            created.errors.rejectValue('parameters', "${runner.name}.$key", message)
        }

        if (created.hasErrors()) {
            return created
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

}
