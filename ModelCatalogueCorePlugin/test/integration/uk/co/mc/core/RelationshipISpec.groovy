package uk.co.mc.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 04/02/2014.
 */


class RelationshipISpec extends Specification{

    @Shared
    def cd1, md1

    def setup(){
        cd1 =  new ConceptualDomain(name: 'element1').save()
        md1 = new Model(name:'element2').save()
    }

    def "Fail to Create Relationship if the catalogue elements have not been persisted"()
    {

        expect:
        Relationship.list().isEmpty()

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

        Relationship rel =  Relationship.link( DE1, DE2, relType)

        then:

        rel.hasErrors()

    }

    def "Create Relationship if the catalogue elements have been persisted and add relations to the source and destination"()
    {

        expect:
        Relationship.list().isEmpty()

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

        expect:
        Relationship.list().isEmpty()

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

        expect:
        Relationship.list().isEmpty()

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


    def "single test for context relationship typs"(){

        RelationshipType.initDefaultRelationshipTypes()

        expect:
        Relationship.list().isEmpty()

        when:

        Relationship rel=new Relationship([source: cd1,destination:md1,relationshipType: RelationshipType.contextType]);
        rel.save()

        then:
        !rel.hasErrors() == true

    }

    @Unroll
    def "uk.co.mc.core.Relationship creation for #args results #validates"()
    {
        RelationshipType.initDefaultRelationshipTypes()

        expect:
        Relationship.list().isEmpty()

        when:
        Relationship rel=new Relationship(args);
        rel.save()

        then:
        !rel.hasErrors() == validates

        where:

        validates  | args
        false      | [ : ]
        false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2')]
        false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: RelationshipType.contextType]
        true       | [source:cd1,destination:md1,relationshipType: RelationshipType.contextType]
        false      | [source:new DataElement(name: 'elementb1'),destination:new DataElement(name:'element2'),relationshipType: RelationshipType.containmentType]
        true       | [source:new Model(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: RelationshipType.containmentType]
        false      | [source:new DataElement(name: 'parentModel'),destination:new Model(name:'model2'),relationshipType: RelationshipType.hierarchyType]
        true       | [source:new Model(name: 'parentModel'),destination:new Model(name:'model2'),relationshipType: RelationshipType.hierarchyType]
        false      | [source:new DataElement(name: 'parentModel'),destination:new Model(name:'model2'),relationshipType:  RelationshipType.inclusionType]
        true       | [source:new ConceptualDomain(name: 'element1'),destination:new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name:"MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?",  description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")), relationshipType:  RelationshipType.inclusionType]
        false      | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: RelationshipType.instantiationType]
        true       | [source:new DataElement(name: 'element1'),destination:new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name:"MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?",  description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")), relationshipType: RelationshipType.instantiationType]
        // false       | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new Mapping()]
        // true       | [source:new EnumeratedType(name: 'universitySubjects', enumerations: ['history', 'politics', 'science']), destination:new EnumeratedType(name: 'publicSubjects', enumerations: ['HIS', 'POL', 'SCI']),relationshipType: new Mapping(map: ['history':'HIS', 'politics':'POL', 'science':'SCI'])]
        false      | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: new RelationshipType(name: "BroaderTerm", sourceClass: DataElement, destinationClass: DataElement, destinationToSource: "narrower terms", sourceToDestination: "broader term for")]
        true       | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new RelationshipType(name: "BroaderTerm", sourceClass: DataElement, destinationClass: DataElement, destinationToSource: "narrower terms", sourceToDestination: "broader term for")]
        false      | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: RelationshipType.supersessionType]
        true       | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: RelationshipType.supersessionType]

    }

}
