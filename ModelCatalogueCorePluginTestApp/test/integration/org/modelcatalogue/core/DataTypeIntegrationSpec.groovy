package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec

class DataTypeIntegrationSpec extends IntegrationSpec {

    def "validateRule"() {
        given:
        DataType dataType = new DataType(rule: "x == null || x in ['red', 'blue']")

        expect:
        !dataType.validateRule('yellow')

        and:
        dataType.validateRule('red')
    }
}
