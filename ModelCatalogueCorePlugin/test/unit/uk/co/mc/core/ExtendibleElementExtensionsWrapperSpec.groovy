package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

@Mock([DataElement, ExtensionValue, ExtendibleElement])
class ExtendibleElementExtensionsWrapperSpec extends Specification {


    def "Extendible elements have live map to extension values"() {
        expect:
        !ExtensionValue.count()

        when:
        DataElement element = new DataElement(name: "element").save()

        then:
        element
        !element.extensions
        element.ext != null
        element.ext.size() == 0
        element.ext.isEmpty()

        when:
        element.ext.foo = "bar"

        then:
        element.extensions
        element.extensions.size() == 1
        ExtensionValue.count() == 1
        element.ext.keySet() == ['foo'] as Set
        element.ext.values()?.contains('bar')
        element.ext.entrySet() == [foo: 'bar'].entrySet()
        // ExtensionValue.findByElementAndName(element, 'foo')
        element.ext.containsKey('foo')
        element.ext.containsValue('bar')
        element.ext.size() == 1

        when:

        String oldVal = element.ext.put('foo', "barbar")

        then:
        element.extensions
        element.extensions.size() == 1
        element.ext.containsKey('foo')
        element.ext.containsValue('barbar')
        !element.ext.containsValue('bar')
        element.ext.size() == 1
        oldVal == "bar"

        when:
        element.ext.x = "something"

        then:
        IllegalArgumentException e = thrown()
        e.message == "Invalid key: x. The key must be contain at least two characters"


        when:
        element.ext.putAll(one: "1", two: "2")

        then:
        element.extensions.size() == 3
        element.ext.containsKey("one")
        element.ext.containsKey("two")
        element.ext.size() == 3


        when:
        element.ext.remove("one")

        then:
        element.extensions.size() == 2
        !element.ext.containsKey("one")
        element.ext.containsKey("two")
        element.ext.size() == 2

        when:
        element.ext.clear()

        then:
        !element.extensions
        !element.ext.containsKey("one")
        !element.ext.containsKey("two")
        !element.ext.size()

    }


}
