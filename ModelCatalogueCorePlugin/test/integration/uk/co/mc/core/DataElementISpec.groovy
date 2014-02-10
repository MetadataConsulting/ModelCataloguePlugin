package uk.co.mc.core

import spock.lang.Specification

/**
 * Created by adammilward on 05/02/2014.
 */

class DataElementISpec extends Specification{

    def "create a new data element, finalize it and then try to change it"(){

        expect:

        DataElement.list().isEmpty()

        when:

        DataElement dataElementInstance = new DataElement(name: "result1", description: "this is the the result description")
        dataElementInstance.save()

        then:

        !dataElementInstance.hasErrors()

        when:

        dataElementInstance.status = PublishedElement.Status.FINALIZED
        dataElementInstance.save(flush:true)

        then:

        !dataElementInstance.hasErrors()

        when:

        dataElementInstance.status = PublishedElement.Status.PENDING
        dataElementInstance.save()

        then:

        dataElementInstance.hasErrors()
        dataElementInstance.errors.getFieldError("status")?.code =='validator.finalized'

    }

    def "create two data elements with the same code dataElement"(){

        expect:

        DataElement.list().isEmpty()

        when:

        DataElement dataElementInstance1 = new DataElement(name: "result1", description: "this is the the result description", code: "x123")
        dataElementInstance1.save(flush:true)

        DataElement dataElementInstance2 = new DataElement(name: "result2", description: "this is the the result2 description", code: "x123")
        dataElementInstance2.validate()

        then:

        dataElementInstance2.hasErrors()

    }


    def "get all relations"() {
        RelationshipType.initDefaultRelationshipTypes()
        DataElement one     = new DataElement(name: "one")
        DataElement two     = new DataElement(name: "two")
        DataElement three   = new DataElement(name: "three")
        DataElement four    = new DataElement(name: "four")

        [one, two, three, four]*.save()

        expect:

        !one.hasErrors()    || !one.errors
        !two.hasErrors()    || !two.errors
        !three.hasErrors()  || !three.errors
        !four.hasErrors()   || !four.errors

        one.createLinkTo(two, RelationshipType.supersessionType)
        three.createLinkFrom(two, RelationshipType.supersessionType)

        one.relations
        one.relations.size()    == 1
        two.relations
        two.relations.size()    == 2
        three.relations
        three.relations.size()  == 1

        two     in one.relations

        one     in two.relations
        three   in two.relations

        two     in three.relations

        two.getRelationsByType(RelationshipType.supersessionType).size() == 2
        two.getIncomingRelationsByType(RelationshipType.supersessionType).size() == 1
        two.getOutgoingRelationsByType(RelationshipType.supersessionType).size() == 1

        !two.getRelationsByType(RelationshipType.containmentType)
        !two.getIncomingRelationsByType(RelationshipType.containmentType)
        !two.getOutgoingRelationsByType(RelationshipType.containmentType)

        one.removeLinkTo(two, RelationshipType.supersessionType)
        three.removeLinkFrom(two, RelationshipType.supersessionType)

        !one.relations
        !two.relations
        !three.relations

    }


}
