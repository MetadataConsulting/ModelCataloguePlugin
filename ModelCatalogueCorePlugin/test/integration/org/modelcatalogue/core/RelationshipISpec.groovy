package org.modelcatalogue.core

import org.modelcatalogue.core.util.RelationshipDirection
import spock.lang.Unroll

/**
 * Created by adammilward on 04/02/2014.
 */


class RelationshipISpec extends AbstractIntegrationSpec{

    def relationshipService

    def setup(){
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
        RelationshipType.findByName("relationship")
    }

    ValueDomain getVd1() {
        ValueDomain.findByName("school subject")
    }


    def "Fail to Create Relationship if the catalogue elements have not been persisted"()
    {


        when:
        RelationshipType hierarchy = RelationshipType.findByName("hierarchy")
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
        de2.countIncomingRelationsByType(reltype) == 1
        de1.countOutgoingRelationsByType(reltype) == 1


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


    @Unroll
    def "testNumber #testNumber org.modelcatalogue.core.Relationship creation for #args results #validates"() {

        when:
        Map<String, String> ext = args.remove('ext')
        Relationship rel = new Relationship(args);
        rel.save()

        if (ext) {
            assert rel.errors.errorCount == 0
            rel.ext = ext
            rel.validate()
        }

        then:
        (rel.errors.errorCount == 0) == validates


        cleanup:
        if (rel.id) rel.delete()

        where:
        testNumber | validates  | args
        1  | false | [:]
        2  | false | [source: new DataElement(name: 'element1'), destination: de1]
        5  | false | [source: new DataElement(name: 'element1'), destination: de1, relationshipType: RelationshipType.containmentType]
        6  | true  | [source: md1, destination: de1, relationshipType: RelationshipType.containmentType]
        7  | false | [source: new DataElement(name: 'parentModel'), destination: md1, relationshipType: RelationshipType.hierarchyType]
        8  | true  | [source: md1, destination: md1, relationshipType: RelationshipType.hierarchyType]
        14 | true  | [source: de1, destination: de2, relationshipType: reltype]
        16 | true  | [source: de1, destination: de2, relationshipType: RelationshipType.supersessionType]
        17 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Min Occurs': -1]]
        18 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Max Occurs': -1]]
        19 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Min Occurs': 0]]
        20 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Min Occurs': 3, 'Max Occurs': 5]]
        21 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Min Occurs': 5, 'Max Occurs': 5]]
        22 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Min Occurs': 6, 'Max Occurs': 5]]
        23 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Max Occurs': 0]]
        24 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Min Occurs': 1, 'Max Occurs': 'unbounded']]
        25 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Min Occurs': 'unbounded', 'Max Occurs': 'unbounded']]

    }

    Model getMd1() {
        Model.findByName("book")
    }

    Model getMd1() {
        Model.findByName("book")
    }


    def "get classified name"() {
        expect:
        relationshipService.getClassifiedName(null)                     ==  null
        relationshipService.getClassifiedName(new Model(name: 'BLAH'))  == 'BLAH'

        when:
        Classification classification = new Classification(name: "classy").save(failOnError: true)
        Model model = new Model(name: "Supermodel").save(failOnError: true)
        model.addToClassifications(classification)

        then:
        relationshipService.getClassifiedName(model) == 'Supermodel (classy)'

        cleanup:
        classification?.delete()
        model?.delete()
    }

    def "get classification info"() {
        expect:
        relationshipService.getClassificationsInfo(null)                     == []
        relationshipService.getClassificationsInfo(new Model(name: 'BLAH'))  == []

        when:
        Classification classification = new Classification(name: "classy").save(failOnError: true)
        Model model = new Model(name: "Supermodel").save(failOnError: true)
        model.addToClassifications(classification)

        def info = relationshipService.getClassificationsInfo(model)

        then:
        info == [
                [name: classification.name, id: classification.id, elementType: Classification.name, link: "/classification/${classification.id}", status: 'DRAFT']
        ]

        cleanup:
        classification?.delete()
        model?.delete()
    }


    @Unroll
    def "init default indexes assigned when linking sortable relationship type so we are able to reorder (#direction)"() {
        given:
        RelationshipType type = RelationshipType.relatedToType
        Model m1 = new Model(name: 'M1').save(failOnError: true)

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

    def "only one relationship is created for bidirectional relationship type"() {
        Relationship rel1 = relationshipService.link(de1, de2, RelationshipType.synonymType)
        Relationship rel2 = relationshipService.link(de2, de1, RelationshipType.synonymType)

        expect:
        rel1 == rel2
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

    private List<Long> getIds(RelationshipDirection direction, RelationshipType type, Model m1) {
        relationshipService.getRelationships([sort: direction.sortProperty], direction, m1, type).items*.id
    }

}
