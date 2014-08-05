package org.modelcatalogue.core.actions

import grails.test.mixin.Mock
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

/**
 * Created by ladin on 04.08.14.
 */
@Mock([Action, ActionDependency])
class ActionServiceSpec extends Specification {

    ActionService service = new ActionService()

    def "performing action does all necessary steps"() {
        def queue = []
        ExecutorService executorService = Mock(ExecutorService)
        executorService.submit(_ as Runnable) >> { Runnable task -> queue.add task ; new FutureTask<String>({"BLAH"})}

        service.executorService =  executorService


        Action action = createAction()

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

    private static Action createAction(Map<String, String> parameters = [test: 'ok']) {
        createAction(parameters, ActionState.PENDING)
    }
    private static Action createAction(Map<String, String> parameters = [test: 'ok'], ActionState state) {
        Action action = new Action()
        action.state = state
        action.parameters = parameters
        action.actionClass = TestActionRunner
        action.save(failOnError: true)
        action
    }


    def "action is dismissed"() {
        Action action = createAction(should: 'dismiss')

        expect:
        action.state == ActionState.PENDING

        when:
        service.dismiss(action)

        then:
        action.state == ActionState.DISMISSED
    }

    def "create new action using service"() {
        Action one = createAction(role: "one")
        Action two = createAction(role: "two")

        Action created = service.create(TestActionRunner, one, two, role: 'created')

        expect:
        created
        created.parameters
        created.parameters.size() == 1
        created.parameters.role == 'created'
        created.dependsOn
        created.dependsOn.size() == 2
        created.dependsOn.any { it.provider == one }
        created.dependsOn.any { it.provider == two }
        created.state == ActionState.PENDING
    }

    def "action parameters are validated before saving"() {
        Action failed = service.create(TestActionRunner, fail: "it")

        expect:
        failed.hasErrors()
        failed.errors.hasFieldErrors('parameters')
        failed.errors.getFieldErrorCount('parameters') == 2
        failed.errors.getFieldError('parameters').defaultMessage == "This would fail!"
        failed.errors.getFieldError('parameters').code == "${TestActionRunner.name}.fail"
    }

    def "list actions"() {
        createAction()
        createAction(ActionState.DISMISSED)
        createAction(ActionState.PERFORMED)

        expect:
        service.list(ActionState.PENDING).total == 1
        service.list(ActionState.DISMISSED).total == 1
        service.list(ActionState.PERFORMED).total == 1
        service.list(ActionState.FAILED).total == 0
        service.list(ActionState.PERFORMING).total == 0
    }

}

class TestActionRunner extends AbstractActionRunner {
    @Override void run() { out << "performed with $parameters" }
    @Override Map<String, String> validate(Map<String, String> params) {
        if (params.fail) {
            return [fail: "This would fail!", error: "Also emits error"]
        }
        super.validate(params)
    }
}
