package org.modelcatalogue.core.actions

import grails.test.spock.IntegrationSpec
import grails.util.Holders
import org.modelcatalogue.core.util.lists.ListWithTotalAndType

import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

class ActionServiceSpec extends IntegrationSpec {

    ActionService service
    Batch batch

    def setup() {
        service = Holders.applicationContext.getBean(ActionService)
        batch = new Batch(name: "FirstTestSpec Batch").save(failOnError: true)
    }

    def "performing action does all necessary steps"() {
        def queue = []
        ExecutorService executorService = Mock(ExecutorService)
        executorService.submit(_ as Runnable) >> { Runnable task ->
            queue.add task; new FutureTask<ActionResult>({
                new ActionResult(outcome: "BLAH", failed: false)
            })
        }

        service.executorService = executorService


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
        for (task in queue) {
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

        dependant.addToDependsOn(provider: ok, dependant: dependant, role: 'test')
        dependant.addToDependsOn(provider: fail, dependant: dependant, role: 'blah')

        when:
        service.run dependant

        then:
        dependant.state == ActionState.FAILED
    }

    def "circular dependency fails the actions"() {
        setupExecutorServiceForImmediateExecution()

        Action one = createAction()
        Action two = createAction()
        Action three = createAction()

        two.addToDependsOn(provider: one, dependant: two, role: 'two')
        three.addToDependsOn(provider: two, dependant: three, role: 'three')
        one.addToDependsOn(provider: three, dependant: one, role: 'one')

        when:
        service.run one

        then:
        one.state == ActionState.FAILED
        one.outcome == "Action failed because at least one of the dependencies failed. The error from the dependency follows:\n\nCircular dependency found: ${one.id} -> ${three.id} -> ${two.id} -> ${one.id}".toString()
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

    private Action createAction(Map<String, String> parameters = [test: 'ok']) {
        createAction(parameters, ActionState.PENDING)
    }

    private Action createAction(Map<String, String> parameters = [test: 'ok'], ActionState state) {
        Action action = new Action()
        action.state = state
        action.type = TestActionRunner
        action.batch = batch
        action.save(failOnError: true)

        action.ext.putAll parameters

        batch.addToActions(action)

        action
    }


    def "action is dismissed"() {
        Action action = createAction(should: 'dismiss')
        Action dependant = createAction(is: 'dependant')

        action.addToDependencies(dependant: dependant, provider: action, role: 'test')
        dependant.addToDependsOn(dependant: dependant, provider: action, role: 'foo')

        expect:
        action.state == ActionState.PENDING
        dependant.state == ActionState.PENDING

        when:
        service.dismiss(action)

        then:
        action.state == ActionState.DISMISSED
        dependant.state == ActionState.DISMISSED

        when:
        service.reactivate(dependant)

        then:
        dependant.state == ActionState.PENDING
        action.state == ActionState.PENDING
    }

    def "create new action using service"() {
        Action one = createAction(role: "one")
        Action two = createAction(role: "two")

        Action created = service.create(batch, TestActionRunner, one: one, two: two, role: 'created')

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
        Action failed = service.create(batch, TestActionRunner, fail: "it")

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

        Batch other = new Batch(name: "Other batch").save(failOnError: true)

        expect:
        service.list(batch, ActionState.PENDING).total == 1
        service.list(batch, ActionState.DISMISSED).total == 1
        service.list(batch, ActionState.PERFORMED).total == 1
        service.list(batch, ActionState.FAILED).total == 0
        service.list(batch, ActionState.PERFORMING).total == 0

        service.list(other, ActionState.PENDING).total == 0
        service.list(other, ActionState.DISMISSED).total == 0
        service.list(other, ActionState.PERFORMED).total == 0
        service.list(other, ActionState.FAILED).total == 0
        service.list(other, ActionState.PERFORMING).total == 0
    }

    def "find by type and params"() {
        createAction(one: 'one', two: 'two')
        createAction(one: 'one', two: 'two', ActionState.FAILED)
        createAction(one: 'one')
        createAction(two: 'two')
        createAction(one: 'one', ActionState.DISMISSED)
        createAction(two: 'two', ActionState.PERFORMED)

        Batch other = new Batch(name: "Other batch").save(failOnError: true)

        when:
        ListWithTotalAndType<Action> allTestAndPending = service.listByTypeAndParams(batch, TestActionRunner)
        ListWithTotalAndType<Action> otherTestAndPending = service.listByTypeAndParams(other, TestActionRunner)

        then:
        allTestAndPending.total == 3
        otherTestAndPending.total == 0

        when:
        ListWithTotalAndType<Action> allTestAndOne = service.listByTypeAndParams(batch, TestActionRunner, one: 'one')
        ListWithTotalAndType<Action> otherTestAndOne = service.listByTypeAndParams(other, TestActionRunner, one: 'one')

        then:
        allTestAndOne.total == 2
        otherTestAndOne.total == 0


        when:
        ListWithTotalAndType<Action> allTestOneAndTwo = service.listByTypeAndParams(batch, TestActionRunner, one: 'one', two: 'two')
        ListWithTotalAndType<Action> otherTestOneAndTwo = service.listByTypeAndParams(other, TestActionRunner, one: 'one', two: 'two')

        then:
        allTestOneAndTwo.total == 1
        otherTestOneAndTwo.total == 0
    }

    def "reuse existing action"() {
        Action first = service.create batch, TestActionRunner, one: 12345
        Action second = service.create batch, TestActionRunner, one: 12345

        expect:
        first == second

        when:
        Action third = service.create batch, TestActionRunner, two: 345, action: first
        Action fourth = service.create batch, TestActionRunner, two: 345, action: second
        Action dep4a5 = service.create(batch, TestActionRunner, one: 56789)
        Action fifth = service.create batch, TestActionRunner, two: 345, action: dep4a5

        then:
        third == fourth
        fourth != fifth
    }

}

class TestActionRunner extends AbstractActionRunner {
    @Override void run() {
        if (parameters.fail) {
            throw new RuntimeException("Failed!")
        }
        out << "performed with $parameters"
    }

    @Override Map<String, String> validate(Map<String, String> params) {
        if (params.fail) {
            return [fail: "This would fail!", error: "Also emits error"]
        }
        super.validate(params)
    }
}
