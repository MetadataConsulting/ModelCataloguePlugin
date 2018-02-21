package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(ValidationRuleService)
class ValidationRuleServiceSpec extends Specification {

    def "validating of DataElement is extracted"() {
        when:
        DataElement dataElement = new DataElement(dataType: new DataType(rule: "x == null || x in ['red', 'blue']"))
        def result = service.validatingByCatalogueElement(dataElement)

        then:
        result != null
        result.implicitRule == "x == null || x in ['red', 'blue']"
        result.explicitRule == null
        result.bases.isEmpty()
    }
}

