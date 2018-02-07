package org.modelcatalogue.core.actions

import grails.test.mixin.Mock
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Mock(BatchController)
class ArchiveCommandSpec extends Specification {

    @Shared
    @Subject
    ArchiveCommand cmd

    void setup() {
        cmd = new ArchiveCommand()
    }

    void 'mappingSuggestionIds cannot be null'() {
        when:
        cmd.batchIds = null

        then:
        !cmd.validate(['batchIds'])
        cmd.errors['batchIds'].code == 'nullable'
    }

    void 'batchIds cannot be an empty list'() {
        when:
        cmd.batchIds = [1]

        then:
        cmd.validate(['batchIds'])

        when:
        cmd.batchIds = []

        then:
        !cmd.validate(['batchIds'])
        cmd.errors['batchIds'].code == 'minSize.notmet'

    }

}
