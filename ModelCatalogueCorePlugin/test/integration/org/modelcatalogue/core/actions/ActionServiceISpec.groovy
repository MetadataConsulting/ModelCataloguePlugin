package org.modelcatalogue.core.actions

import grails.test.spock.IntegrationSpec

class ActionServiceISpec extends IntegrationSpec {

    ActionService actionService

    def "runners are injected with dependencies"() {
        Action action = actionService.create(new Batch(name: "test batch").save(failOnError: true), IntegrationTestActionRunner)

        actionService.run(action)

        expect:
        action.outcome != "ElementService is null"
    }

}