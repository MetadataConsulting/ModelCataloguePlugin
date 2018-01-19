package org.modelcatalogue.core.actions

import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@TestFor(BatchController)
class GenerateSuggestionsCommandSpec extends Specification {

    @Shared
    @Subject
    GenerateSuggestionsCommand cmd

    def setupSpec() {
        cmd = new GenerateSuggestionsCommand()
    }

    def cleanupSpec() {
        cmd = null
    }

    void 'dataModel1ID cannot be null'() {
        when:
        cmd.dataModel1ID = null

        then:
        !cmd.validate(['dataModel1ID'])
    }

    void 'dataModel2ID cannot be null'() {
        when:
        cmd.dataModel2ID = null

        then:
        !cmd.validate(['dataModel2ID'])
    }

    void 'minScore cannot be null'() {
        when:
        cmd.minScore = null

        then:
        !cmd.validate(['minScore'])
    }

    void 'minScore cannot be greater than 100'() {
        when:
        cmd.minScore = 101

        then:
        !cmd.validate(['minScore'])
    }

    void 'minScore cannot be lower than 100'() {
        when:
        cmd.minScore = -1

        then:
        !cmd.validate(['minScore'])
    }
}
