package org.modelcatalogue.core.actions

import groovy.util.logging.Log4j

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

        Long id = action.id

        ActionRunner runner = action.actionClass.newInstance()
        runner.initWith(action.parameters)

        action.state = ActionState.PERFORMING
        action.save(failOnError: true)

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


}
