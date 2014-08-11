package org.modelcatalogue.core.actions

import org.modelcatalogue.core.AbstractIntegrationSpec


class ActionServiceISpec extends AbstractIntegrationSpec {

    ActionService actionService

    def "runners are injected with dependencies"() {
        Action action = actionService.create(new Batch(name: "test batch").save(failOnError: true), IntegrationTestActionRunner)

        actionService.run(action)

        expect:
        action.outcome != "PublishedElementService is null"
    }

}