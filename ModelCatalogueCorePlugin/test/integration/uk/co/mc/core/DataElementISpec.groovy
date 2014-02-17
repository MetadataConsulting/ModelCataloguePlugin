package uk.co.mc.core

import grails.test.spock.IntegrationSpec
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by adammilward on 05/02/2014.
 */

class DataElementISpec extends IntegrationSpec{

    @Shared
    def fixtureLoader, auth1, auth3, auth2

    def setup(){
        def fixtures =  fixtureLoader.load( "dataElements/DE_author1", "dataElements/DE_author2", "dataElements/DE_author3")

        auth1 = fixtures.DE_author1
        auth2 = fixtures.DE_author2
        auth3 = fixtures.DE_author3

    }

    def cleanup(){
        auth1.delete()
        auth2.delete()
        auth3.delete()

    }

    def "create a new data element, finalize it and then try to change it"(){

        when:

        def dataElementInstance = new DataElement(name: "result1", description: "this is the the result description")
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

        dataElementInstance.delete()

    }

    def "create writer data elements with the same code dataElement"(){

        when:

        def dataElementInstance2 = new DataElement(name: "result2", description: "this is the the result2 description", code: "XXX_1")
        dataElementInstance2.validate()

        then:

        dataElementInstance2.hasErrors()

    }


    def "get all relations"() {

        RelationshipType.initDefaultRelationshipTypes()

        when:
        def author1 = DataElement.get(auth1.id)
        def author2 = DataElement.get(auth2.id)
        def author3 =  DataElement.get(auth3.id)

        then:

        !author1.hasErrors()    || !author.errors
        !author2.hasErrors()    || !writer.errors
        !author3.hasErrors()    || !title.errors

        when:

        author2.createLinkTo(author1, RelationshipType.supersessionType)
        author2.createLinkFrom(author3, RelationshipType.supersessionType)

        then:

        author1.relations
        author1.relations.size()    == 1
        author2.relations
        author2.relations.size()    == 2
        author3.relations
        author3.relations.size()  == 1

        author2     in author1.relations

        author1     in author2.relations
        author3      in author2.relations

        author2     in author3.relations

        author2.getRelationsByType(RelationshipType.supersessionType).size() == 2
        author2.getIncomingRelationsByType(RelationshipType.supersessionType).size() == 1
        author2.getOutgoingRelationsByType(RelationshipType.supersessionType).size() == 1

        !author2.getRelationsByType(RelationshipType.containmentType)
        !author2.getIncomingRelationsByType(RelationshipType.containmentType)
        !author2.getOutgoingRelationsByType(RelationshipType.containmentType)

        println(author3.relations)
        println(author2.relations)
        println(author1.relations)


        when:

        author2.removeLinkTo(author1, RelationshipType.supersessionType)
        author2.removeLinkFrom(author3, RelationshipType.supersessionType)


        author2.save(flush:true)

        then:

        author1.relations==[]
        author2.relations==[]
        author3.relations==[]


    }


}
