package org.modelcatalogue.core

import spock.lang.Shared
import spock.lang.Unroll

/**
 * Created by adammilward on 04/02/2014.
 */


class RelationshipISpec extends AbstractIntegrationSpec{

    @Shared
    def cd1, md1, de1, vd1, de2, reltype, dt, ms, relationshipService

    def setupSpec(){

        loadFixtures()

        cd1 =  ConceptualDomain.findByName("public libraries")
        md1 = Model.findByName("book")
        de1 = DataElement.findByName("auth5")
        de2 = DataElement.findByName("title")
        dt = DataType.findByName("string")
        ms = MeasurementUnit.findByName("Miles per hour")
        reltype = RelationshipType.findByName("relationship")
        vd1 = ValueDomain.findByName("school subject")
    }

    /*def cleanupSpec(){

        cd1.delete()
        de1.delete()
        de2.delete()
        dt.delete()
        ms.delete()
        md1.delete()
        reltype.delete()
        vd1.delete()
    }*/

    def "Fail to Create Relationship if the catalogue elements have not been persisted"()
    {


        when:
        RelationshipType hierarchy = RelationshipType.findByName("hierarchy")
        Relationship rel =  relationshipService.link( de1, de2, hierarchy)

        then:
        rel.hasErrors()

    }

    def "Create Relationship if the catalogue elements have been persisted then delete relationship"()
    {


        when:

        de1 = DataElement.get(de1.id)
        de2 = DataElement.get(de2.id)

        Relationship rel =  relationshipService.link( de1, de2, reltype)

        then:

        !rel.hasErrors()
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

        !rel1.hasErrors()
        !rel2.hasErrors()

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
        3  | false | [source: new DataElement(name: 'element1'), destination: de1, relationshipType: RelationshipType.contextType]
        4  | true  | [source: cd1, destination: md1, relationshipType: RelationshipType.contextType]
        5  | false | [source: new DataElement(name: 'element1'), destination: de1, relationshipType: RelationshipType.containmentType]
        6  | true  | [source: md1, destination: de1, relationshipType: RelationshipType.containmentType]
        7  | false | [source: new DataElement(name: 'parentModel'), destination: md1, relationshipType: RelationshipType.hierarchyType]
        8  | true  | [source: md1, destination: md1, relationshipType: RelationshipType.hierarchyType]
        9  | false | [source: new DataElement(name: 'parentModel'), destination: md1, relationshipType: RelationshipType.inclusionType]
        10 | true  | [source: cd1, destination: vd1, relationshipType: RelationshipType.inclusionType]
        11 | false | [source: new ConceptualDomain(name: 'element1'), destination: md1, relationshipType: RelationshipType.instantiationType]
        12 | true  | [source: de1, destination: vd1, relationshipType: RelationshipType.instantiationType]
        13 | false | [source: new ConceptualDomain(name: 'element1'), destination: md1, relationshipType: RelationshipType.instantiationType]
        14 | true  | [source: de1, destination: de2, relationshipType: reltype]
        15 | false | [source: cd1, destination: md1, relationshipType: RelationshipType.supersessionType]
        16 | true  | [source: de1, destination: de2, relationshipType: RelationshipType.supersessionType]
        17 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Source Min Occurs': -1]]
        18 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Source Max Occurs': -1]]
        19 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Source Min Occurs': 0]]
        20 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Source Min Occurs': 3, 'Source Max Occurs': 5]]
        21 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Source Min Occurs': 5, 'Source Max Occurs': 5]]
        22 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Source Min Occurs': 6, 'Source Max Occurs': 5]]
        23 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Source Max Occurs': 0]]
        24 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Destination Min Occurs': -1]]
        25 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Destination Min Occurs': -1]]
        26 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Destination Min Occurs': 0]]
        27 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Destination Min Occurs': 3, 'Destination Max Occurs': 5]]
        28 | true  | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Destination Min Occurs': 5, 'Destination Max Occurs': 5]]
        29 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Destination Min Occurs': 6, 'Destination Max Occurs': 5]]
        30 | false | [source: md1, destination: de2, relationshipType: RelationshipType.containmentType, ext: ['Destination Max Occurs': 0]]


    }

}
