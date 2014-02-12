package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@Mock([ExtensionValue, DataElement])
class ExtensionValueSpec extends Specification {

    @Shared
    DataElement de = new DataElement(name: "element")

    def setup() { de.save() }

    def cleanup() { de.delete() }

    @Unroll
    def "create a mew extension value from #args validates to #validates"() {

        expect:

        ExtensionValue.list().isEmpty()

        when:

        ExtensionValue type = new ExtensionValue(args)
        type.save()


        then:

        !type.hasErrors() == validates
        ExtensionValue.list().size() == size

        where:
        validates | size | args
        false     | 0    | [:]
        false     | 0    | [name: "x" * 256]
        false     | 0    | [name: "x" * 256, value: "x"]
        false     | 0    | [name: "x" * 256, value: "x" * 1001]
        false     | 0    | [name: "x" * 256, value: "x" * 1001]
        false     | 0    | [name: "x" * 256, element: de]
        false     | 0    | [name: "x" * 256, value: "x", element: de]
        false     | 0    | [name: "x" * 256, value: "x" * 1001, element: de]
        false     | 0    | [name: "x" * 256, value: "x" * 1001, element: de]
        true      | 1    | [name: "String", value: "x", element: de]

    }
}
