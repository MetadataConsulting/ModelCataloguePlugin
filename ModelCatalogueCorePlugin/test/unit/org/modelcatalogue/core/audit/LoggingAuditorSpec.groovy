package org.modelcatalogue.core.audit

import spock.lang.Specification

class LoggingAuditorSpec extends Specification {


    void "read problematic value"() {
        when:
            LoggingAuditor.readValue("""{"name":"http://forms.modelcatalogue.org/section#exclude","relationship":{"id":131300,"source":
{"semanticVersion":"1.4.1","name":"test rd condition 01","id":46380,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/46380","versionNumber":2,"latestVersionId":45132,"classifiedName":"test rd condition 01 (Rare Disease Conditions)"}
,"destination":
{"semanticVersion":"1.4.1","name":"BBS Clinical Tests","id":45249,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/45249","versionNumber":6,"latestVersionId":28096,"classifiedName":"BBS Clinical Tests (Rare Disease Conditions)"}
,"type":
{"id":4,"name":"hierarchy","link":"/relationshipType/4"}
,"elementType":"org.modelcatalogue.core.Relationship"}}""")

        then:
            noExceptionThrown()
    }
}
