package org.modelcatalogue.core

import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import spock.lang.Unroll

class RelationshipISpec extends AbstractIntegrationSpec{

    def relationshipService

    def setup() {
        loadFixtures()
    }

    DataElement getDe1() {
        DataElement.findByName("auth5")
    }

    DataElement getDe2() {
        DataElement.findByName("title")
    }

    DataType getDt() {
        DataType.findByName("string")
    }

    MeasurementUnit getMs() {
        MeasurementUnit.findByName("Miles per hour")
    }

    RelationshipType getReltype() {
        RelationshipType.readByName("relationship")
    }

    def "Fail to Create Relationship if the catalogue elements have not been persisted"()
    {


        when:
        RelationshipType hierarchy = RelationshipType.readByName("hierarchy")
        Relationship rel =  relationshipService.link( de1, de2, hierarchy)

        then:
        rel.errors.errorCount > 0

    }

    def "Create Relationship if the catalogue elements have been persisted then delete relationship"()
    {
        when:
        Relationship rel =  relationshipService.link( de1, de2, reltype)

        then:

        rel.errors.errorCount == 0
        de2.getIncomingRelations().contains(de1)
        de1.getOutgoingRelations().contains(de2)
        de2.getIncomingRelationsByType(reltype).contains(de1)
        de1.getOutgoingRelationsByType(reltype).contains(de2)
        de1.getRelationsByType(reltype).contains(de2)
        de2.countIncomingRelationshipsByType(reltype) == 1
        de1.countOutgoingRelationshipsByType(reltype) == 1


        when:

        relationshipService.unlink( de1, de2, reltype)

        then:
        de2.getIncomingRelations() == []
        de1.getOutgoingRelations() == []
    }


    def "Duplicated relationship test"()
    {

        when:

        Relationship rel1 =  relationshipService.link( de1, de2, reltype)
        Relationship rel2 =  relationshipService.link( de1, de2, reltype)

        then:

        rel1.errors.errorCount == 0
        rel2.errors.errorCount == 0

        rel1==rel2

        de2.getIncomingRelations()
        de1.getOutgoingRelations()

        when:

        relationshipService.unlink( de1, de2, reltype)

        then:

        de2.getIncomingRelations() == []
        de1.getOutgoingRelations() == []

    }

    DataClass getMd1() {
        DataClass.findByName("book")
    }


    def "get classified name"() {
        expect:
        CatalogueElementMarshaller.getClassifiedName(null)                     ==  null
        CatalogueElementMarshaller.getClassifiedName(new DataClass(name: 'BLAH'))  == 'BLAH'

        when:
        DataModel classification = new DataModel(name: "classy").save(failOnError: true)
        DataClass model = new DataClass(dataModel: classification, name: "Supermodel").save(failOnError: true)

        then:
        CatalogueElementMarshaller.getClassifiedName(model) == 'Supermodel (classy 0.0.1)'

        cleanup:
        classification?.delete()
        model?.delete()
    }

    def "get classification info"() {
        expect:
        relationshipService.getDataModelsInfo(null)                     == []
        relationshipService.getDataModelsInfo(new DataClass(name: 'BLAH'))  == []

        when:
        DataModel classification = new DataModel(name: "classy").save(failOnError: true)
        DataClass model = new DataClass(dataModel: classification, name: "Supermodel").save(failOnError: true)

        def info = relationshipService.getDataModelsInfo(model)

        then:
        info == [
                [name: classification.name, id: classification.id, elementType: DataModel.name, link: "/dataModel/${classification.id}", status: 'DRAFT']
        ]

        cleanup:
        classification?.delete()
        model?.delete()
    }


    @Unroll
    def "init default indexes assigned when linking sortable relationship type so we are able to reorder (#direction)"() {
        given:
        RelationshipType type = RelationshipType.relatedToType
        DataClass m1 = new DataClass(name: 'M1').save(failOnError: true)

        DataElement de1 = new DataElement(name: "DE1").save(failOnError: true)
        DataElement de2 = new DataElement(name: "DE2").save(failOnError: true)
        DataElement de3 = new DataElement(name: "DE3").save(failOnError: true)


        when:
        Relationship m1de1 = link(direction, m1, de1, type)

        then:
        !m1de1.errors.errorCount
        direction.getIndex(m1de1)

        when:
        Relationship m1de2 = link(direction, m1, de2, type)

        then:
        !m1de2.errors.errorCount
        direction.getIndex(m1de2)
        direction.getIndex(m1de1) < direction.getIndex(m1de2)

        when:
        Relationship m1de3 = link(direction, m1, de3, type)

        then:
        !m1de3.errors.errorCount
        direction.getIndex(m1de3)
        direction.getIndex(m1de2) < direction.getIndex(m1de3)

        expect: "relationships are sorted from first to the third"
        getIds(direction, type, m1) == [m1de1, m1de2, m1de3]*.id

        when: "first relationship is moved after the third"
        printIndexes direction, 'before move', m1de1: m1de1, m2de2: m1de2, m1de3: m1de3
        m1de1 = relationshipService.moveAfter(direction, m1, m1de1, m1de3)
        printIndexes direction, 'after move', m1de1: m1de1, m2de2: m1de2, m1de3: m1de3
        !m1de1.errors.errorCount

        then: "the index of first is bigger than the third one"
        direction.getIndex(m1de1) > direction.getIndex(m1de3)
        getIds(direction, type, m1) == [m1de2, m1de3, m1de1]*.id

        when: "there is no element to move after"
        printIndexes direction, 'before move', m1de1: m1de1, m2de2: m1de2, m1de3: m1de3
        m1de1 = relationshipService.moveAfter(direction, m1, m1de1, null)
        printIndexes direction, 'after move', m1de1: m1de1, m2de2: m1de2, m1de3: m1de3

        then: "then it will be moved to the beginning"
        !m1de1.errors.errorCount
        direction.getIndex(m1de1) < direction.getIndex(m1de3)
        direction.getIndex(m1de1) < direction.getIndex(m1de2)
        getIds(direction, type, m1) == [m1de1, m1de2, m1de3]*.id

        when: "there is no gap between the relationships"
        direction.setIndex(m1de1, 1)
        direction.setIndex(m1de2, 2)
        direction.setIndex(m1de3, 3)

        [m1de1, m1de2, m1de3]*.save(failOnError: true)

        m1de1 = relationshipService.moveAfter(direction, m1, m1de1, m1de2)

        then: "the index of first is bigger than the second one"
        direction.getIndex(m1de1) > direction.getIndex(m1de2)

        and: "the index of first is smaller than the third one"
        direction.getIndex(m1de1) < direction.getIndex(m1de3)

        when: "all indexes are zero (legacy database)"
        direction.setIndex(m1de1, 0)
        direction.setIndex(m1de2, 0)
        direction.setIndex(m1de3, 0)

        [m1de1, m1de2, m1de3]*.save(failOnError: true)

        m1de1 = relationshipService.moveAfter(direction, m1, m1de1, m1de2)

        then: "the index of first is bigger than the second one"
        direction.getIndex(m1de1) > direction.getIndex(m1de2)

        when: "some indexes are negative"
        direction.setIndex(m1de1, -1000)
        direction.setIndex(m1de2, 0)
        direction.setIndex(m1de3, 1000)

        [m1de1, m1de2, m1de3]*.save(failOnError: true)

        m1de1 = relationshipService.moveAfter(direction, m1, m1de1, m1de2)

        then: "the index of first is bigger than the second one"
        direction.getIndex(m1de1) > direction.getIndex(m1de2)

        and: "the index of first is smaller than the third one"
        direction.getIndex(m1de1) < direction.getIndex(m1de3)

        where:
        direction << RelationshipDirection.values()
    }

    def "opposite direction relationship is created for bidirectional relationship type"() {
        expect:
        RelationshipType.synonymType.bidirectional

        when:
        RelationshipDefinition definition = RelationshipDefinition.create(de1, de2, RelationshipType.synonymType).definition

        // create new relationship which is bidirectional
        Relationship rel1 = relationshipService.link(definition)

        // opposite direction already exists
        Relationship rel2 = relationshipService.findExistingRelationship(definition.inverted())

        // so it will return the same instance
        Relationship rel3 = relationshipService.link(definition.inverted())

        then:
        rel1 != rel2
        rel2 == rel3
    }

    private static void printIndexes(Map<String, Relationship> relationships, RelationshipDirection direction, String label) {
        println "$label:\n    ${relationships.sort {entry -> direction.getIndex(entry.value) } collect { key, value -> "$key: ${direction.getIndex(value)}"} join(', ')}"
    }

    private Relationship link(RelationshipDirection direction, CatalogueElement source, CatalogueElement destination, RelationshipType type) {
        if (direction == RelationshipDirection.INCOMING) {
            return relationshipService.link(destination, source, type)
        }
        return relationshipService.link(source, destination, type)
    }

    private List<Long> getIds(RelationshipDirection direction, RelationshipType type, DataClass m1) {
        relationshipService.getRelationships([sort: direction.sortProperty], direction, m1, type).items*.id
    }

}
