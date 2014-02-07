package uk.co.mc.core

import spock.lang.Specification

/**
 * Created by adammilward on 04/02/2014.
 */
class RelationshipISpec extends Specification{

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



}
