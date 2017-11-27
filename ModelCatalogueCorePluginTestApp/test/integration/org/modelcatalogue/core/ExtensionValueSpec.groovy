package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Ignore
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class ExtensionValueSpec extends IntegrationSpec{

    @Ignore
    @Unroll
    def "#r: create a new extension value from #args validates to #validates"() {
        int initialSize = ExtensionValue.count()
        when:

        ExtensionValue type = new ExtensionValue(args)
        type.save()

        println type.errors

        then:

        !type.hasErrors() == validates
        ExtensionValue.count() == size + initialSize

        where:
        r | validates | size | args
        1 | false     | 0    | [:]
        2 | false     | 0    | [name: "x" * 256]
        3 | false     | 0    | [name: "x" * 256, extensionValue: "x"]
        4 | false     | 0    | [name: "x" * 256, extensionValue: "x" * 1001]
        5 | false     | 0    | [name: "x" * 256, element: de]
        6 | true      | 1    | [name: "xxx", element: de]
        7 | false     | 0    | [name: "xxx" * 256, extensionValue: "x", element: de]
        8 | false     | 0    | [name: "xxx" * 256, extensionValue: "x" * 1001, element: de]
        9 | true      | 1    | [name: "xxx", extensionValue: "x", element: de]
    }

    DataElement getDe() {
        new DataElement(name: "element").save(failOnError: true)
    }


}
