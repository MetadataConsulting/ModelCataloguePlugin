package org.modelcatalogue.core.actions

import groovy.util.logging.Log4j

import java.util.concurrent.ExecutorService

/**
 * Created by ladin on 04.08.14.
 */
@Log4j
class ActionService {

    ExecutorService executorService

    def run(Action action) {
        if (action.state != ActionState.PENDING) {
            return
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
            } catch (e) {
                log.warn("Exception executing action $action.actionClass with parameters $action.parameters", e)
            }

        })

    }


}
