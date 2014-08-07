package org.modelcatalogue.core.actions

import grails.test.mixin.Mock
import org.modelcatalogue.core.util.ListWithTotalAndType
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

@Mock([Action, ActionDependency, ActionParameter])
class ActionServiceSpec extends Specification {

    ActionService service = new ActionService()

    def "performing action does all necessary steps"() {
        def queue = []
        ExecutorService executorService = Mock(ExecutorService)
        executorService.submit(_ as Runnable) >> { Runnable task -> queue.add task ; new FutureTask<ActionResult>({ new ActionResult(outcome: "BLAH", failed: false) })}

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

    def "failing provider action will fails the dependant"() {
        setupExecutorServiceForImmediateExecution()

        Action ok = createAction()
        Action fail = createAction(fail: 'true')
        Action dependant = createAction(role: 'dependant')

        dependant.addToDependsOn(provider: ok, dependant: dependant)
        dependant.addToDependsOn(provider: fail, dependant: dependant)

        when:
        service.run dependant

        then:
        dependant.state == ActionState.FAILED
    }

    def "circular dependency fails the actions"(){
        setupExecutorServiceForImmediateExecution()

        Action one = createAction()
        Action two = createAction()
        Action three = createAction()

        two.addToDependsOn(provider: one, dependant: two)
        three.addToDependsOn(provider: two, dependant: three)
        one.addToDependsOn(provider: three, dependant: one)

        when:
        service.run one

        then:
        one.state == ActionState.FAILED
        one.outcome == "Action failed because at least one of the dependencies failed. The error from the dependency follows:\n\nCircular dependency found: 1 -> 3 -> 2 -> 1"
    }

    private void setupExecutorServiceForImmediateExecution() {
        ExecutorService executorService = Mock(ExecutorService)
        executorService.submit(_ as Runnable) >> { Runnable task ->
            FutureTask<ActionResult> future = new FutureTask<ActionResult>(task)
            future.run()
            future
        }

        service.executorService = executorService
    }

    private static Action createAction(Map<String, String> parameters = [test: 'ok']) {
        createAction(parameters, ActionState.PENDING)
    }
    private static Action createAction(Map<String, String> parameters = [test: 'ok'], ActionState state) {
        Action action = new Action()
        action.state = state
        action.type = TestActionRunner
        action.save(failOnError: true)

        action.ext.putAll parameters

        action
    }


    def "action is dismissed"() {
        Action action = createAction(should: 'dismiss')
        Action dependant = createAction(is: 'dependant')

        action.addToDependencies(dependant: dependant, provider: action)

        expect:
        action.state == ActionState.PENDING
        dependant.state == ActionState.PENDING

        when:
        service.dismiss(action)

        then:
        action.state == ActionState.DISMISSED
        dependant.state == ActionState.DISMISSED
    }

    def "create new action using service"() {
        Action one = createAction(role: "one")
        Action two = createAction(role: "two")

        Action created = service.create(TestActionRunner, one, two, role: 'created')

        expect:
        created
        created.ext
        created.ext.size() == 1
        created.ext.role == 'created'
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
        failed.errors.hasFieldErrors('extensions')
        failed.errors.getFieldErrorCount('extensions') == 2
        failed.errors.getFieldError('extensions').defaultMessage == "This would fail!"
        failed.errors.getFieldError('extensions').code == "${TestActionRunner.name}.fail"
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

    def "find by type and params"() {
        createAction(one: 'one', two: 'two')
        createAction(one: 'one', two: 'two', ActionState.FAILED)
        createAction(one: 'one')
        createAction(two: 'two')
        createAction(one: 'one', ActionState.DISMISSED)
        createAction(two: 'two', ActionState.PERFORMED)

        when:
        ListWithTotalAndType<Action> allTestAndPending = service.listByTypeAndParams(TestActionRunner)

        then:
        allTestAndPending.total == 3

        when:
        ListWithTotalAndType<Action> allTestAndOne = service.listByTypeAndParams(TestActionRunner, one: 'one')

        then:
        allTestAndOne.total == 2


        when:
        ListWithTotalAndType<Action> allTestOneAndTwo = service.listByTypeAndParams(TestActionRunner, one: 'one', two: 'two')

        then:
        allTestOneAndTwo.total == 1
    }

}

class TestActionRunner extends AbstractActionRunner {
    @Override void run() {
        if (parameters.fail) {
            throw new RuntimeException("Failed!")
        }
        out << "performed with $parameters"
    }

    @Override
    String getMessage() {
        return "Test message"
    }

    @Override
    String getDescription() {
        return "Description"
    }

    @Override Map<String, String> validate(Map<String, String> params) {
        if (params.fail) {
            return [fail: "This would fail!", error: "Also emits error"]
        }
        super.validate(params)
    }
}
