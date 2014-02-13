package uk.co.mc.core

import grails.test.spock.IntegrationSpec
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by adammilward on 05/02/2014.
 */

class DataElementISpec extends IntegrationSpec{

    @Shared
    def fixtureLoader, author, title, writer

    def setupSpec(){
        def fixtures =  fixtureLoader.load( "dataElements/author", "dataElements/writer",
                "dataElements/title",)

        author = fixtures.author
        writer = fixtures.writer
        title = fixtures.title
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
//has the same code as author
        def dataElementInstance2 = new DataElement(name: "result2", description: "this is the the result2 description", code: "XXX")
        dataElementInstance2.validate()

        then:

        dataElementInstance2.hasErrors()

    }


    def "get all relations"() {
        
        RelationshipType.initDefaultRelationshipTypes()

        expect:

        !author.hasErrors()    || !author.errors
        !writer.hasErrors()    || !writer.errors
        !title.hasErrors()  || !title.errors

        author.createLinkTo(writer, RelationshipType.supersessionType)
        title.createLinkFrom(writer, RelationshipType.supersessionType)

        author.relations
        author.relations.size()    == 1
        writer.relations
        writer.relations.size()    == 2
        title.relations
        title.relations.size()  == 1

        writer     in author.relations

        author     in writer.relations
        title      in writer.relations

        writer     in title.relations

        writer.getRelationsByType(RelationshipType.supersessionType).size() == 2
        writer.getIncomingRelationsByType(RelationshipType.supersessionType).size() == 1
        writer.getOutgoingRelationsByType(RelationshipType.supersessionType).size() == 1

        !writer.getRelationsByType(RelationshipType.containmentType)
        !writer.getIncomingRelationsByType(RelationshipType.containmentType)
        !writer.getOutgoingRelationsByType(RelationshipType.containmentType)

        author.removeLinkTo(writer, RelationshipType.supersessionType)
        title.removeLinkFrom(writer, RelationshipType.supersessionType)

        !author.relations
        !writer.relations
        !title.relations


    }


}
