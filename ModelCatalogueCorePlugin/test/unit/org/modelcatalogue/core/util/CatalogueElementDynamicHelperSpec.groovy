package org.modelcatalogue.core.util

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

@TestFor(InitCatalogueService)
@Mock([RelationshipType, TestCatalogueElement1, TestCatalogueElement2, Relationship])
class CatalogueElementDynamicHelperSpec extends Specification {
    // TODO: try to move to integration folder so we get rid off all the @Mock tests

    @Unroll @ConfineMetaClassChanges([TestCatalogueElement1, TestCatalogueElement2])
    def "Relationships from #clazz are added to the transients so they are #transients"() {
        CatalogueElementDynamicHelper.addShortcuts(clazz)

        expect:
        clazz.transients
        clazz.transients as Set == transients as Set

        where:
        clazz                   | transients
        TestCatalogueElement1 | [*new ArrayList<String>(CatalogueElement.transients), 'parentOf', 'childOf', 'synonyms']
        TestCatalogueElement2 | [*new ArrayList<String>(CatalogueElement.transients), 'parentOf', 'childOf', 'synonyms', 'br', 'dr']

    }

    @Unroll @ConfineMetaClassChanges([TestCatalogueElement1, TestCatalogueElement2])
    def "Relationship #prop is added to #clazz"() {

        service.initDefaultRelationshipTypes()

        CatalogueElementDynamicHelper.addShortcuts(clazz)

        def instance = clazz.newInstance()

        expect:
        instance.hasProperty(prop)
        instance[prop]                          == []
        instance[prop + 'Relationships']        == []
        instance."count${prop.capitalize()}"()  == 0


        where:
        clazz                   | prop
        TestCatalogueElement1   | 'parentOf'
        TestCatalogueElement1   | 'synonyms'
        TestCatalogueElement1   | 'childOf'
        TestCatalogueElement2   | 'parentOf'
        TestCatalogueElement2   | 'childOf'
        TestCatalogueElement2   | 'br'
        TestCatalogueElement2   | 'dr'

    }

    @ConfineMetaClassChanges(TestCatalogueElement2)
    def "Link and unlink"() {
        RelationshipService service = new RelationshipService()
        AuditService auditService = Mock(AuditService)
        CatalogueElementDynamicHelper.addShortcuts(TestCatalogueElement2)
        RelationshipType type = new RelationshipType(name: 'a', sourceToDestination: "a to b", destinationToSource: "b to a", sourceClass: CatalogueElement, destinationClass: CatalogueElement)
        type.relationshipTypeService = new RelationshipTypeService()

        expect:
        type.save()

        when:
        TestCatalogueElement2 first     = [name: "First"]
        TestCatalogueElement2 second    = [name: "Second"]

        first.relationshipService       = service
        first.auditService              = auditService

        second.relationshipService      = service
        second.auditService             = auditService

        then:
        first.save()
        second.save()

        when:
        Relationship relationship = first.addToBr(second)

        then:
        relationship
        relationship.id
        relationship.source             == second
        relationship.destination        == first
        relationship.relationshipType   == type

        when:
        Relationship relationship2 = first.removeFromBr(second)

        then:
        relationship == relationship2 || !relationship2

    }

}

class TestCatalogueElement1 extends CatalogueElement {

    static relationships = [
        incoming:       [hierarchy: 'parentOf'],
        outgoing:       [hierarchy: 'childOf'],
        bidirectional:  [synonym: 'synonyms']
    ]

}

class TestCatalogueElement2 extends TestCatalogueElement1 {

    static relationships = [
        incoming: [a: 'br'],
        outgoing: [c: 'dr']
    ]

}
