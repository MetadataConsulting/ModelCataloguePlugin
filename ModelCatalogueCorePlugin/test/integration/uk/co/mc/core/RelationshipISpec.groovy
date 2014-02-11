package uk.co.mc.core

import org.hibernate.TransientObjectException
import org.springframework.dao.InvalidDataAccessApiUsageException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 04/02/2014.
 */


class RelationshipISpec extends Specification{

    @Shared
    def cd1, md1, de1, vd1, de2, reltype, dt, ms

    def setupSpec(){

        RelationshipType.initDefaultRelationshipTypes()

        cd1 =  new ConceptualDomain(name: 'element1').save()
        md1 = new Model(name:'element2').save()
        de1 = new DataElement(name:'data element1').save()
        de2 = new DataElement(name:'element2').save()
        dt = new DataType(name: "Float").save()
        ms = new MeasurementUnit(name:"MPH").save()
        reltype = new RelationshipType(name: "BroaderTerm", sourceClass: DataElement, destinationClass: DataElement, destinationToSource: "narrower terms", sourceToDestination: "broader term for").save()
        vd1 = new ValueDomain(name: "ground_speed", unitOfMeasure: ms, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?",  description: "the ground speed of the moving vehicle", dataType: dt).save()
    }

    def cleanupSpec(){

        Relationship.list().each{ relationship ->

            Relationship.unlink(relationship.source, relationship.destination, relationship.relationshipType)
        }

        cd1.delete()
        md1.delete()
        de1.delete()
        de2.delete()
        vd1.delete()
    }

    def "Fail to Create Relationship if the catalogue elements have not been persisted"()
    {


        when:

        RelationshipType relType = new RelationshipType(
                sourceToDestination: "Parent",
                destinationToSource: "Child",
                name: "Child Type",
                sourceClass: DataElement,
                destinationClass: DataElement
        )


        Relationship rel =  Relationship.link( de1, de2, relType)

        then:

        rel.hasErrors()

    }

    def "Create Relationship if the catalogue elements have been persisted and add relations to the source and destination"()
    {

        when:

        RelationshipType relType = new RelationshipType(
                sourceToDestination: "Parent",
                destinationToSource: "Child",
                name: "Child Type",
                sourceClass: DataElement,
                destinationClass: DataElement
        )


        DataElement DE1 = new DataElement(name:"test2DE")
        DataElement DE2 = new DataElement(name:"test1DE")

        then:

        relType.save()
        DE1.save()
        DE2.save()



        Relationship rel =  Relationship.link( DE1, DE2, relType)

        then:

        !rel.hasErrors()

        when:
        DE1 = DataElement.get(DE1.id)
        DE2 = DataElement.get(DE2.id)

        then:
        DE1.outgoingRelationships?.contains(rel)
        DE2.incomingRelationships?.contains(rel)


        !DE2.outgoingRelationships?.contains(rel)
        !DE1.incomingRelationships?.contains(rel)

    }


    def "Duplicated relationship test"()
    {

        when:

        RelationshipType relType = new RelationshipType(
                sourceToDestination: "Parent",
                destinationToSource: "Child",
                name: "Child Type",
                sourceClass: DataElement,
                destinationClass: DataElement
        )


        DataElement DE1 = new DataElement(name:"test2DE")
        DataElement DE2 = new DataElement(name:"test1DE")

        then:

        relType.save()
        DE1.save()
        DE2.save()



        Relationship rel1 =  Relationship.link( DE1, DE2, relType)
        Relationship rel2 =  Relationship.link( DE1, DE2, relType)

        then:

        !rel1.hasErrors()
        !rel2.hasErrors()

        rel1==rel2

    }

    def "Unlink relationship"()
    {

        when:

        RelationshipType relType = new RelationshipType(
                sourceToDestination: "Parent",
                destinationToSource: "Child",
                name: "Child Type",
                sourceClass: DataElement,
                destinationClass: DataElement
        )


        DataElement DE1 = new DataElement(name:"test2DE")
        DataElement DE2 = new DataElement(name:"test1DE")

       then:
        relType.save()
        DE1.save()
        DE2.save()

        when:
        Relationship rel1 =  Relationship.link( DE1, DE2, relType)

        then:
        !rel1.hasErrors()


        when:
        Relationship.unlink(DE1,DE2,relType)

        then:
        !DE1.outgoingRelationships.contains(rel1)
        !DE2.incomingRelationships.contains(rel1)



    }



    @Unroll
    def "testNumber #testNumber uk.co.mc.core.Relationship creation for #args results #validates"()
    {

        when:
        Relationship rel=new Relationship(args);
        rel.save()

        then:
        !rel.hasErrors() == validates || rel.errors


        where:

        testNumber | validates  | args
        1          |false      | [ : ]
        2          |false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2')]
        3          |false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: RelationshipType.contextType]
        4          |true       | [source:cd1,destination:md1,relationshipType: RelationshipType.contextType]
        5          |false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: RelationshipType.containmentType]
        6          |true       | [source:md1,destination:de1,relationshipType: RelationshipType.containmentType]
        7          |false      | [source:new DataElement(name: 'parentModel'),destination:new Model(name:'model2'),relationshipType: RelationshipType.hierarchyType]
        8          |true       | [source:md1,destination:md1,relationshipType: RelationshipType.hierarchyType]
        9          |false      | [source:new DataElement(name: 'parentModel'),destination:new Model(name:'model2'),relationshipType:  RelationshipType.inclusionType]
        10          |true       | [source:cd1,destination:vd1, relationshipType:  RelationshipType.inclusionType]
        11          | false      | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: RelationshipType.instantiationType]
        12          |true       | [source:de1,destination:vd1, relationshipType: RelationshipType.instantiationType]
        // false       | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new Mapping()]
        // true       | [source:new EnumeratedType(name: 'universitySubjects', enumerations: ['history', 'politics', 'science']), destination:new EnumeratedType(name: 'publicSubjects', enumerations: ['HIS', 'POL', 'SCI']),relationshipType: new Mapping(map: ['history':'HIS', 'politics':'POL', 'science':'SCI'])]
        13          |false      | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: new RelationshipType(name: "BroaderTerm", sourceClass: DataElement, destinationClass: DataElement, destinationToSource: "narrower terms", sourceToDestination: "broader term for")]
        14          |true       | [source:de1,destination:de2,relationshipType: reltype]
        15          |false      | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: RelationshipType.supersessionType]
        16          |true       | [source:de1,destination:de2,relationshipType: RelationshipType.supersessionType]

    }

}
