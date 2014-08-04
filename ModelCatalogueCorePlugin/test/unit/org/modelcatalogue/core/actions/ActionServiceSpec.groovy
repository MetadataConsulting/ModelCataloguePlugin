package org.modelcatalogue.core.actions

import grails.test.mixin.Mock
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

/**
 * Created by ladin on 04.08.14.
 */
@Mock(Action)
class ActionServiceSpec extends Specification {

    def "performing action does all necessary steps"() {
        def queue = []
        ExecutorService executorService = Mock(ExecutorService)
        executorService.submit(_ as Runnable) >> { Runnable task -> queue.add task ; new FutureTask<String>({"BLAH"})}

        ActionService service = new ActionService(executorService: executorService)


        Action action = new Action()
        action.parameters = [test: 'ok']
        action.actionClass = TestActionRunner
        action.save(failOnError: true)

        expect:
        action.state == ActionState.PENDING
        action.outcome == null

        when:
        service.run(action)
        action = Action.get(action.id)

        then:
        action.state == ActionState.PERFORMING

        when:
        for(task in queue) {
            task.run()
        }
        action = Action.get(action.id)

        then:
        action.outcome == "performed with [test:ok]"
        action.state == ActionState.PERFORMED
    }

}

class TestActionRunner extends AbstractActionRunner { @Override void run() { out << "performed with $parameters" } }
