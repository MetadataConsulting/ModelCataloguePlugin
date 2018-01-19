package org.modelcatalogue.core.mappingsuggestions

import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@TestFor(MappingSuggestionsController)
class MappingSuggestionApproveCommandConstraintsSpec extends Specification {

    @Shared
    @Subject
    MappingSuggestionApproveCommand cmd

    def setupSpec() {
        cmd = new MappingSuggestionApproveCommand()
    }

    def cleanupSpec() {
        cmd = null
    }

    void 'mappingSuggestionIds cannot be null'() {
        when:
        cmd.mappingSuggestionIds = null

        then:
        !cmd.validate(['mappingSuggestionIds'])
        cmd.errors['mappingSuggestionIds'].code == 'nullable'
    }

    void 'mappingSuggestionIds cannot be an empty list'() {
        when:
        cmd.mappingSuggestionIds = [1]

        then:
        cmd.validate(['mappingSuggestionIds'])

        when:
        cmd.mappingSuggestionIds = []

        then:
        !cmd.validate(['mappingSuggestionIds'])
        cmd.errors['mappingSuggestionIds'].code == 'minSize.notmet'

    }

    void 'batchId cannot be null'() {
        when:
        cmd.batchId = null

        then:
        !cmd.validate(['batchId'])
        cmd.errors['batchId'].code == 'nullable'
    }
}
