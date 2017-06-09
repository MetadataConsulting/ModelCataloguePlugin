package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll

class ExtendibleElementExtensionsWrapperSpec extends IntegrationSpec {

    def relationshipService

    @Unroll
    def "Extendible elements have live map to extension values for #extensionClass"() {

        int initialSize = extensionClass.count()

        when:
        def element = newElementFactory.call().save(flush: true)


        then:
        element
        !element[extensionProperty]
        element.ext != null
        element.ext.size() == 0
        element.ext.isEmpty()

        when:
        element.ext.foo = "bar"

        then:
        element[extensionProperty]
        element[extensionProperty].size() == 1
        extensionClass.count() == 1 + initialSize
        element.ext.keySet() == ['foo'] as Set
        element.ext.values()?.contains('bar')
        element.ext.entrySet() == [foo: 'bar'].entrySet()
        // extensionClass.findByElementAndName(element, 'foo')
        element.ext.containsKey('foo')
        element.ext.containsValue('bar')
        element.ext.size() == 1

        when:

        String oldVal = element.ext.put('foo', "barbar")
        def ex = extensionClass.findByName("foo")

        then:
        element[extensionProperty]
        element[extensionProperty].size() == 1
        element.ext.containsKey('foo')
        element.ext.containsValue('barbar')
        !element.ext.containsValue('bar')
        element.ext.size() == 1
        oldVal == "bar"
        ex
        ex[ownerProperty] == element


        when:
        element.ext."" = "something"

        then:
        IllegalArgumentException e = thrown()
        e.message == "Invalid key: . The key must be contain at least one character! (value = something)"



        when:
        element.ext.putAll(one: "1", two: "2")

        then:
        element[extensionProperty].size() == 3
        element.ext.containsKey("one")
        element.ext.containsKey("two")
        element.ext.size() == 3


        when:
        element.ext.remove("one")

        then:
        element[extensionProperty].size() == 2
        !element.ext.containsKey("one")
        element.ext.containsKey("two")
        element.ext.size() == 2

        when:
        def x =  element.ext
        def xt = element[extensionProperty]
        element.ext.clear()
        x =  element.ext
        xt = element[extensionProperty]


        then:
        !element[extensionProperty]
        !element.ext.containsKey("one")
        !element.ext.containsKey("two")
        !element.ext.size()


        where:
        extensionClass          | extensionProperty     | ownerProperty     | newElementFactory
        ExtensionValue          | "extensions"          | 'element'         | this.&createDataElement
        RelationshipMetadata    | "extensions"          | 'relationship'    | this.&createRelationship
    }

    private DataElement createDataElement() {
        new DataElement(name: "element").save()
    }

    private Relationship createRelationship() {
        DataClass source = new DataClass(name: "source")
        assert source.save()

        DataClass target = new DataClass(name: "target")
        assert target.save()

        RelationshipType type = new RelationshipType(sourceToDestination: "src to dest", destinationToSource: "dest to src", sourceClass: CatalogueElement, destinationClass: CatalogueElement, name: "type")
        assert type.save()


        def rel = relationshipService.link(source, target, type)

        assert !rel.errors.hasErrors()

        rel
    }


}
