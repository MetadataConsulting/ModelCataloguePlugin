package org.modelcatalogue.core.dataarchitect

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(CSVService)
class CSVServiceSpec extends Specification {

    void "read csv headers"() {
        String inputFile = """
        one;two;three
        1;2;3
    """.stripIndent().trim()

        Reader stringReader = new StringReader(inputFile)

        String[] headers = service.readHeaders(stringReader)

        expect:
        headers
        headers.length == 3
        headers[0] == 'one'
        headers[1] == 'two'
        headers[2] == 'three'
    }
}
