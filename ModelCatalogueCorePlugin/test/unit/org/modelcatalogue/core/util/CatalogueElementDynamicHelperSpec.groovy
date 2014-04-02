package org.modelcatalogue.core.util

import grails.test.mixin.Mock
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipService
import org.modelcatalogue.core.RelationshipType
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

@Mock([RelationshipType, TestCatalogueElement1, TestCatalogueElement2, Relationship])
class CatalogueElementDynamicHelperSpec extends Specification {

    @Unroll @ConfineMetaClassChanges([TestCatalogueElement1, TestCatalogueElement2])
    def "Relationships from #clazz are added to the transients so they are #transients"() {
        CatalogueElementDynamicHelper.addShortcuts(clazz)

        expect:
        clazz.transients
        clazz.transients == transients

        where:
        clazz                   | transients
        TestCatalogueElement1   | ['relations', 'info', 'hasContextOf', 'parentOf', 'childOf']
        TestCatalogueElement2   | ['relations', 'info', 'hasContextOf', 'parentOf', 'childOf', 'b', 'd']

    }

    @Unroll @ConfineMetaClassChanges([TestCatalogueElement1, TestCatalogueElement2])
    def "Relationship #prop is added to #clazz"() {
        RelationshipType.initDefaultRelationshipTypes()

        CatalogueElementDynamicHelper.addShortcuts(clazz)

        def instance = clazz.newInstance()

        expect:
        instance.hasProperty(prop)
        instance[prop]                          == []
        instance."count${prop.capitalize()}"()  == 0


        where:
        clazz                   | prop
        TestCatalogueElement1   | 'hasContextOf'
        TestCatalogueElement1   | 'parentOf'
        TestCatalogueElement1   | 'childOf'
        TestCatalogueElement2   | 'hasContextOf'
        TestCatalogueElement2   | 'parentOf'
        TestCatalogueElement2   | 'childOf'
        TestCatalogueElement2   | 'b'
        TestCatalogueElement2   | 'd'

    }

    @ConfineMetaClassChanges(TestCatalogueElement2)
    def "Link and unlink"() {
        RelationshipService service = new RelationshipService()
        CatalogueElementDynamicHelper.addShortcuts(TestCatalogueElement2)
        RelationshipType type = new RelationshipType(name: 'a', sourceToDestination: "a to b", destinationToSource: "b to a", sourceClass: CatalogueElement, destinationClass: CatalogueElement)

        expect:
        type.save()

        when:
        TestCatalogueElement2 first     = [name: "First"]
        TestCatalogueElement2 second    = [name: "Second"]

        first.relationshipService       = service
        second.relationshipService      = service

        then:
        first.save()
        second.save()

        when:
        Relationship relationship = first.addToB(second)

        then:
        relationship
        relationship.id
        relationship.source             == second
        relationship.destination        == first
        relationship.relationshipType   == type

        when:
        Relationship relationship2 = first.removeFromB(second)

        then:
        relationship == relationship2 || !relationship2

    }

}

class TestCatalogueElement1 extends CatalogueElement {

    static relationships = [
        incoming: [context: 'hasContextOf', hierarchy: 'parentOf'],
        outgoing: [hierarchy: 'childOf']
    ]

}

class TestCatalogueElement2 extends TestCatalogueElement1 {

    static relationships = [
        incoming: [a: 'b'],
        outgoing: [c: 'd']
    ]

}
