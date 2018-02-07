package org.modelcatalogue.core.mappingsuggestions

import grails.test.mixin.TestFor
import org.modelcatalogue.core.actions.ActionState
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@TestFor(MappingSuggestionsController)
class MappingSuggestionIndexCommandConstraintsSpec extends Specification {

    @Shared
    @Subject
    MappingSuggestionIndexCommand cmd

    def setupSpec() {
        cmd = new MappingSuggestionIndexCommand()
    }

    def cleanupSpec() {
        cmd = null
    }

    void 'batchId cannot be null'() {
        when:
        cmd.batchId = null

        then:
        !cmd.validate(['batchId'])
    }

    void 'state can be null'() {
        when:
        cmd.state = null

        then:
        cmd.validate(['state'])
    }

    void 'state can be a list of APPROVED, REJECTED or PENDING'() {
        when:
        cmd.state = state
        if ( !cmd.validate() ) {
            println cmd.errors
        }

        then:
        cmd.validate(['state'])

        where:
        state << [
                [ActionState.PENDING, ActionState.PERFORMING, ActionState.PERFORMED, ActionState.DISMISSED, ActionState.FAILED],
                [ActionState.PENDING,],
                [ActionState.PERFORMING,],
                [ActionState.PERFORMED,],
                [ActionState.DISMISSED,],
                [ActionState.FAILED],
        ]
    }

    void 'max can be null'() {
        when:
        cmd.max = null

        then:
        cmd.validate(['max'])
    }

    void 'offset can not be lower than 1'() {
        when:
        cmd.offset = 1

        then:
        cmd.validate(['max'])

        when:
        cmd.max = 0

        then:
        !cmd.validate(['max'])
        cmd.errors['max'].code == 'min.notmet'
    }

    void 'offset can be null'() {
        when:
        cmd.offset = null

        then:
        cmd.validate(['offset'])
    }

    void 'offset can not be lower than 0'() {
        when:
        cmd.offset = 0

        then:
        cmd.validate(['offset'])

        when:
        cmd.offset = -1

        then:
        !cmd.validate(['offset'])
        cmd.errors['offset'].code == 'min.notmet'
    }
}
