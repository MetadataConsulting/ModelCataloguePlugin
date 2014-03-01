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
    def "#r: create a mew extension value from #args validates to #validates"() {

        expect:

        ExtensionValue.list().isEmpty()

        when:

        ExtensionValue type = new ExtensionValue(args)
        type.save()

        println type.errors

        then:

        !type.hasErrors() == validates
        ExtensionValue.list().size() == size

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


    def "check  EqualsAndHashCode works"() {

        when:
        def a = new ExtensionValue(name: "xxx", extensionValue: "x", element: de).save()
        def b = new ExtensionValue(name: "xxx", extensionValue: "x", element: de).save()
        def c = new ExtensionValue(name: "xxx1", extensionValue: "x", element: de).save()

        then:

        a.equals(b)
        b.equals(a)
        !a.equals(c)

    }
}
