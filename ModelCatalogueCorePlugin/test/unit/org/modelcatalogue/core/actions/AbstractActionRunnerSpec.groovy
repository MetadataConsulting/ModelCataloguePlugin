package org.modelcatalogue.core.actions

import spock.lang.Specification

class AbstractActionRunnerSpec extends Specification {

    def "it normalizes to natural line breaks"() {
        expect:
        AbstractActionRunner.normalizeDescription("""
            following break will be replaced with space
            as you want to keep paragraphs together.

            But two new lines are interpreted as one new line
                as well as if you indent
        """) == """
            following break will be replaced with space as you want to keep paragraphs together.
            But two new lines are interpreted as one new line
                as well as if you indent
        """.stripIndent().trim()

    }

}
